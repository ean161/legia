var list, month;

$(document).ready(function() {
    $("#export-btn").hide();

    $("#smonth").on("change", async () => {
        month = $(this).find("option:selected").text();
        loadList(month);

        if (month != "") {
            $("#export-btn").show();
        } else {
            $("#export-btn").hide();
        }
    });

    $("#export-btn").on("click", async () => {
        $("#export-btn").hide();
        let res = await api(`/legia/salary/export`, {
            month
        });
        window.location.href = "/legia/salary/download-export";
    })
});

function loadList(month) {
    if (month == null || month == "") {
        return;
    }

    $("#monthTitle").html(month);
    $("#list").show();

    if ($.fn.DataTable.isDataTable("#list")) {
        list.destroy();
    }

    list = $("#list").DataTable({
        processing: true,
        serverSide: true,
        scrollX: true,
        language: datatableLang,
        paging: false,
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

            let url = `/legia/salary/list?month=${month}&page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
            if (search) {
                url += `&search=${encodeURIComponent(search)}`;
            }

            $.getJSON(url, function (res) {
                callback({
                    draw: data.draw,
                    recordsTotal: res.totalElements,
                    recordsFiltered: res.totalElements,
                    data: res.content
                });
            });
        },
        order: [[0, "desc"]],
        columns: [
            {
                data: "id",
                orderable: false,
                render: function (data, type, row) {
                    return row.staff.id;
                }
            },
            {
                data: "staff",
                orderable: false,
                render: function (data, type, row) {
                    return data.name;
                }
            },
            {
                data: "salary",
                orderable: false,
                render: function (data, type, row) {
                    return `${numberFormat(row.staff.salary)}`;
                }
            },
            {
                data: "workedDates",
                orderable: false,
            },
            {
                data: "worktimeSal",
                orderable: false,
                render: function (data, type, row) {
                    return `${numberFormat(row.worktimeSal)}<br><i class="uk-text-danger">${row.worktime} giờ</i>`;
                }
            },
            {
                data: "overtimeSal",
                orderable: false,
                render: function (data, type, row) {
                    return `${numberFormat(row.overtimeSal)}<br><i class="uk-text-danger">${row.overtime} giờ</i>`;
                }
            },
            {
                data: "bonusSal",
                orderable: false,
                render: function (data, type, row) {
                    return `${numberFormat(data)}`;
                }
            },
            {
                data: "subSal",
                orderable: false,
                render: function (data, type, row) {
                    return `${numberFormat(data)}`;
                }
            },
            {
                data: "total",
                orderable: false,
                render: function (data, type, row) {
                    return `<b>${numberFormat(data)}</b>`;
                }
            },
        ]
    });
}