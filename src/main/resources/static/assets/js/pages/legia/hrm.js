var list;
var isBlockMass = false;

$(document).ready(function() {
    browserBack((event) => {
        if (list != null && list != undefined) {
            list.ajax.reload(null, false);
        }
    });

    $("#select-all-btn").on("click", async function () {
        let staffChecks = $(".staff-select");
        let oldState = staffChecks.attr("checked");
        staffChecks.attr("checked", !oldState);

        $("#select-all-btn").text(!oldState ? "Bỏ chọn tất cả" : "Chọn tất cả");
    });

    $.getJSON("/legia/hrm/list?page=0&size=1", function (res) {
        let firstRow = res.content[0];

        let baseColumns = [
            {
                data: "id",
                title: "ID",
                orderable: false,
                render: function (data, type, row) {
                    return `<input type="checkbox" class="uk-checkbox staff-select" id="select-${data}" checked />&ensp;${data}`;
                }
            },
            {
                data: "nameDisplay",
                title: "Tên nhân viên",
                orderable: false,
                render: function (data, type, row) {
                    return `<div class="items-center cursor-pointer flex justify-between">
                        <span onclick="unselectStaff(${data.id})" style="background-color: ${generateColors(data.name)}" class="px-2 py-1">${data.name.toUpperCase()}</span>
                        <div class="flex">
                            <span class="size-4 ml-2 mr-1" onclick="statisticStaff(${data.id}, '${data.name}', '${data.salary}');">
                                <uk-icon icon="chart-column-big"></uk-icon>
                            </span>
                            <span class="size-4 ml-2 mr-1" onclick="editStaff(${data.id}, '${data.name}', ${data.salary});">
                                <uk-icon icon="cog"></uk-icon>
                            </span>
                        </div>
                    </div>`;
                }
            },
            {
                data: "actions",
                title: "Thao tác",
                orderable: false,
                render: function (data, type, row) {
                    if (data.type == "end") {
                        return `<button class="uk-button uk-button-danger" onclick="endWork(${row.id})">${data.workTime}</button>`;
                    }

                    return `<button class="uk-button uk-button-secondary" onclick="startWork(${row.id})">Vào ca</button>`;
                }
            }
        ];

        let dateColumns = Object.keys(firstRow.dates || {}).map(k => ({
            data: k,
            title: k,
            orderable: false,
            render: function (data, type, row, meta) {
                if (data.normal == undefined) {
                    return ``;
                }

                let res = ``;
                if (data.normal != "0s") {
                    res = `<span class="uk-text-info">${data.normal}</span>`;
                }

                if (data.ot != "0s") {
                    res += `${(res != `` ? `<br>` : ``)}<span class="uk-text-danger">${data.ot}</span>`;
                }

                return res;
            }
        }));

        list = $("#list").DataTable({
            processing: true,
            serverSide: true,
            scrollX: true,
            language: datatableLang,
            // paging: false,
            autoWidth: false,
            searching: false,
            columnDefs: [
                { targets: "_all", className: "dt-nowrap" }
            ],
            ajax: function (data, callback, settings) {
                let page = Math.floor(data.start / data.length);
                let size = data.length;
                let sortCol = data.columns[data.order[0].column].data;
                let sortDir = data.order[0].dir;
                let search = data.search.value;

                let url = `/legia/hrm/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
                if (search) {
                    url += `&search=${encodeURIComponent(search)}`;
                }

                $.getJSON(url, function (res) {
                    let rows = res.content.map(row => {
                        let newRow = { ...row };
                        if (row.dates) {
                            Object.keys(row.dates).forEach(k => {
                                newRow[k] = row.dates[k];
                            });
                        }
                        return newRow;
                    });

                    callback({
                        draw: data.draw,
                        recordsTotal: res.totalElements,
                        recordsFiltered: res.totalElements,
                        data: rows
                    });
                });
            },
            columns: [...baseColumns, ...dateColumns]
        });
    });


    $("#add-staff-btn").on("click", () => {
        UIkit.modal("#add-staff").toggle();
    });

    $("#static-money-staff-btn").on("click", () => {
        $("#smname").val(localStorage.getItem("name"));

        UIkit.modal("#edit-staff").toggle();
        UIkit.modal("#static-money-staff").toggle();
    });

    $("#add-staff-submit").on("click", async () => {
        let res = await api("/legia/hrm/add", {
            name: $("#name").val(),
            salary: $("#salary").val()
        });

        if (res.code == 0) {
            return;
        }

        UIkit.modal("#add-staff").toggle();

        $("#name").val("");
        $("#salary").val("0");

        list.ajax.reload(null, false);
    });

    $("#edit-staff-submit").on("click", async () => {
        let res = await api("/legia/hrm/edit", {
            id: localStorage.getItem("id"),
            name: $("#ename").val(),
            salary: $("#esalary").val()
        });

        if (res.code == 0) {
            return;
        }

        UIkit.modal("#edit-staff").toggle();

        $("#eid").val("");
        $("#ename").val("");
        $("#esalary").val("0");

        list.ajax.reload(null, false);
    });

    $("#static-money-staff-submit").on("click", async () => {
        let res = await api("/legia/hrm/create-static-money", {
            id: localStorage.getItem("id"),
            type: $("#smtype").val(),
            note: $("#smnote").val(),
            amount: $("#smamount").val()
        });

        $("#smtype").val("");
        $("#smamount").val(0);
        if (res.code == 0) {
            return;
        }

        UIkit.modal("#static-money-staff").toggle();

        $("#smname").val("");
        $("#smnote").val("");
        $("#smamount").val("0");
    });

    $("#smonth").on("change", async () => {
        $("#sdetail").hide();
        var option = $(this).find('option:selected').text();

        let res = await api("/legia/hrm/statistic", {
            id: localStorage.getItem("id"),
            month: option
        });

        var salaryNum = parseInt(localStorage.getItem("salary"));
        var totalDates = Math.round((res.data.month.end - res.data.month.start) / 60 / 60 / 24);

        var shiftDayCount = [...new Set(
            res.data.shifts.map(item => item.startDisplay.split(' ')[1])
        )];


        var statics = ``;
        var staticsTotal = 0;
        for (let key in res.data.statics) {
            let static = res.data.statics[key];

            statics += `${static.note}: ${numberFormat(static.amount)}<br>`;
            staticsTotal += static.amount;
        }


        var worktime = 0;
        var overtime = 0;
        for (let key in res.data.shifts) {
            let shift = res.data.shifts[key];

            worktime += shift.worktime;
            overtime += shift.overtime;
        }

        worktime = Math.floor(worktime / 60 / 60 * 10) / 10;
        overtime = Math.floor(overtime / 60 / 60 * 10) / 10;

        var worktimeSal = Math.floor(salaryNum * worktime / 234 / 1000) * 1000;
        var overtimeSal = Math.floor(overtime * 20000 / 1000) * 1000;

        $("#sstatic-list").html(statics);
        $("#sstatic-list-total").html(`${staticsTotal} vnd`);
        $("#ssalary").html(numberFormat(localStorage.getItem("salary")));
        // $("#sshifts-count").html(`${shiftDayCount.length}/${totalDates}`);
        // $("#soff-day-count").html(`${totalDates - shiftDayCount.length}`);
        $("#swork-time").html(`${worktime} tiếng`);
        $("#sover-time").html(`${overtime} tiếng`);
        $("#swork-time-salary").html(`${numberFormat(worktimeSal)} vnd`);
        $("#sover-time-salary").html(`${numberFormat(overtimeSal)} vnd`);
        $("#stotal-salary").html(`${numberFormat(worktimeSal + overtimeSal + staticsTotal)} vnd`);
        $("#sdetail").show();
    });

    $("#end-all-work-btn").on("click", async () => {
        let res = await api("/legia/hrm/end-all-work", {}, "GET");

        if (res.code == 0) {
            return;
        }

        list.ajax.reload(null, false);
    });

    $("#start-select-work-btn").on("click", async function () {
        if (isBlockMass)
            return;

        $("#start-select-work-btn").text("Đang vào ca");
        await massAction("START");
        $("#start-select-work-btn").text("Vào ca");
    });

    $("#end-select-work-btn").on("click", async function () {
        if (isBlockMass)
            return;

        $("#end-select-work-btn").text("Đang kết ca");
        await massAction("END");
        $("#end-select-work-btn").text("Kết ca");
    });
});

async function deleteStaff(id) {
    let res = await api("/legia/hrm/delete", {
        id
    });

    if (res.code == 0) {
        return;
    }

    UIkit.modal("#edit-staff").toggle();
    list.ajax.reload(null, false);
}

function editStaff(id, name, salary) {
    UIkit.modal("#edit-staff").toggle();
    localStorage.setItem("id", id);
    localStorage.setItem("name", name);

    $("#ename").val(name);
    $("#esalary").val(salary);
    $("#delete-staff-btn").attr("onclick", `deleteStaff('${id}')`);
}

async function statisticStaff(id, name, salary) {
    $("#smonth").val("");
    $("#sdetail").hide();
    $("#sname").html(name);
    localStorage.setItem("id", id);
    localStorage.setItem("salary", salary)
    UIkit.modal("#statistic-staff").toggle();
}

async function startWork(id) {
    let res = await api("/legia/hrm/start-work", {
        id
    });

    if (res.code == 0) {
        return;
    }

    list.ajax.reload(null, false);
}

async function endWork(id) {
    let res = await api("/legia/hrm/end-work", {
        id
    });

    if (res.code == 0) {
        return;
    }

    list.ajax.reload(null, false);
}

function getSelectedStaff() {
    let selectedStaffs = [];

    document.querySelectorAll(".staff-select").forEach(el => {
        if (!el.checked) {
            return;
        }

        let id = el.id.split("-")[1];
        selectedStaffs.push(id);
    });

    return selectedStaffs;
}

async function massAction(action) {
    isBlockMass = true;
    let res = await api("/legia/hrm/mass-action", {
        ids: getSelectedStaff(),
        action: action
    });

    if (res.code == 0) {
        isBlockMass = false;
        return;
    }

    list.ajax.reload(null, false);
    isBlockMass = false;
}

function generateColors(str) {
    // 1. Lấy 10 ký tự cuối
    const tail = str ? str.slice(-10) : "";

    // 2. Hash
    let h = 5381;
    for (let i = 0; i < tail.length; i++) {
        h = ((h << 5) + h) + tail.charCodeAt(i);
        h = h >>> 0;
    }

    // 3. HSL to HEX
    const hslToHex = (hue, sat, light) => {
        sat /= 100;
        light /= 100;
        const c = (1 - Math.abs(2 * light - 1)) * sat;
        const x = c * (1 - Math.abs(((hue / 60) % 2) - 1));
        const m = light - c / 2;
        let r = 0, g = 0, b = 0;

        if (hue < 60) { r = c; g = x; b = 0; }
        else if (hue < 120) { r = x; g = c; b = 0; }
        else if (hue < 180) { r = 0; g = c; b = x; }
        else if (hue < 240) { r = 0; g = x; b = c; }
        else if (hue < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        const toHex = v => Math.round((v + m) * 255).toString(16).padStart(2, '0');
        return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
    };

    // 4. Tạo hue và saturation từ hash
    const hue = h % 360;
    const sat = 45 + ((h >>> 8) % 41); // 45..85

    // 5️⃣ số lượng màu cố định, ví dụ 5 màu
    const steps = 5;

    const maxLight = 88; // sáng nhất
    const minLight = 18; // tối nhất
    const colors = [];

    for (let i = 0; i < steps; i++) {
        const t = i / (steps - 1);
        const light = Math.round(maxLight * (1 - t) + minLight * t);
        colors.push(hslToHex(hue, sat, light));
    }

    return colors[0];
}

function unselectStaff(id) {
    let elm = $(`#select-${id}`);
    elm.attr("checked", !elm.attr("checked"));
}