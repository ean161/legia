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

            let url = `/license/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "userName", orderable: false },
            { data: "directory", orderable: false },
            { data: "title", orderable: false },
            { data: "note", orderable: false },
            { data: "expiredDateDisplay", orderable: false },
            { data: "createdDateDisplay", orderable: false },
            {
                data: "actions",
                orderable: false,
                render: function (data, type, row) {
                    if (data == true) {
                        return `<button class="uk-button uk-button-secondary" onclick="revokeLicense(${row.id})">Thu hồi</button>`;
                    }

                    return ``;
                }
            }
        ]
    });

    $("#add-license-btn").on("click", () => {
        UIkit.modal("#add-license").toggle();
    });

    $("#add-license-submit").on("click", async () => {
        let res = await api("/license/create", {
            id: $("#id").val(),
            directory: $("#directory").val(),
            title: $("#title").val(),
            note: $("#note").val(),
            icon: $("#icon").val(),
            expiredDate: $("#expiredDate").val()
        });

        if (res.code == 0) {
            return;
        }

        UIkit.modal("#add-license").toggle();

        $("#directory").val("");
        $("#title").val("");
        $("#note").val("");
        $("#icon").val("");
        $("#expiredDate").val("");

        list.ajax.reload(null, false);
    });
});

async function revokeLicense(id) {
    let res = await api("/license/revoke", {
        id
    });

    if (res.code == 0) {
        return;
    }

    list.ajax.reload(null, false);
}