var list;

$(document).ready(function() {
    list = $("#list").DataTable({
        processing: true,
        serverSide: true,
        scrollX: true,
        language: datatableLang,
        columnDefs: [
            { targets: "_all", className: "dt-nowrap" }
        ],
        ajax: function (data, callback, settings) {
            let page = Math.floor(data.start / data.length);
            let size = data.length;
            let sortCol = data.columns[data.order[0].column].data;
            let sortDir = data.order[0].dir;
            let search = data.search.value;

            let url = `/legia/shifts/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "id" },
            { data: "staffName", orderable: false },
            { data: "startDisplay", orderable: false },
            { data: "endDisplay", orderable: false },
            { data: "worktimeDisplay", orderable: false },
            { data: "overtimeDisplay", orderable: false },
            {
                data: "actions",
                title: "Thao tác",
                orderable: false,
                render: function (data, type, row) {
                    if (!row.allowEdit) {
                        return "-";
                    }

                    return `<button class="uk-button uk-button-secondary" onclick="editShift(${row.id}, '${row.staffName}', '${row.start}', '${row.end}')">Sửa</button>`;
                }
            }
        ]
    });

    $("#edit-shift-submit").on("click", async () => {
        let res = await api("/legia/shifts/edit", {
            id: localStorage.getItem("id"),
            start: $("#estart").val(),
            end: $("#eend").val()
        });

        if (res.code === 0) {
            return;
        }

        UIkit.modal("#edit-shift").toggle();

        $("#eid").val("");
        $("#ename").val("");
        $("#estart").val("00:00");
        $("#eend").val("00:00");

        list.ajax.reload(null, false);
    });
});

function editShift(id, name, start, end) {
    var startDate = new Date(start * 1000);
    var endDate = new Date(end * 1000);
    console.log(startDate);

    $("#end-shift-ip").hide();
    UIkit.modal("#edit-shift").toggle();

    if (end != 0) {
        $("#end-shift-ip").show();
        $("#eend").val(`${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`);
    }

    localStorage.setItem("id", id);
    localStorage.setItem("name", name);
    localStorage.setItem("start", start);
    localStorage.setItem("end", end);

    $("#ename").val(name);
    $("#estart").val(`${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`);
}