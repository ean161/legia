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

            let url = `/mcserver-deposit-portal/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "title", orderable: false },
            { data: "description", orderable: false },
            {
                data: "banner",
                orderable: false,
                render: function (data, type, row) {
                    return `<img src="${data}" style="height: 25px"/>`
                }
            },
            { data: "createdDateDisplay", orderable: false },
            { 
                data: "actions",
                orderable: false,
                render: function (data, type, row, meta) {
                    return `<button class="uk-button uk-button-primary mr-2" onclick="editServer(${row.id}, '${row.title}', '${row.description}', '${row.banner}')">Sửa</button><button class="uk-button uk-button-danger" onclick="deleteServer(${row.id})">Xoá</button>`;
                }
            }
        ]
    });

    $("#add-server-btn").on("click", () => {
        localStorage.setItem("id", 0);
        $("#title").val("");
        $("#description").val("");
        $("#banner").val("");
        
        UIkit.modal("#save-server").toggle();
    });

    $("#save-server-submit").on("click", async () => {
        var id = localStorage.getItem("id");
        let res = await api(`/mcserver-deposit-portal/${id == null || id == 0 ? `create` : `edit`}`, {
            id: id,
            title: $("#title").val(),
            description: $("#description").val(),
            banner: $("#banner").val()
        });

        if (res.code == 0) {
            return;
        }

        UIkit.modal("#save-server").toggle();

        $("#title").val("");
        $("#description").val("");
        $("#banner").val("");

        list.ajax.reload(null, false);
    });
});

function editServer(id, title, description, banner) {
    localStorage.setItem("id", id);

    $("#title").val(title);
    $("#description").val(description);
    $("#banner").val(banner);

    UIkit.modal("#save-server").toggle();
}

async function deleteServer(id) {
    let res = await api("/mcserver-deposit-portal/delete", {
        id
    });

    if (res.code == 0) {
        return;
    }

    list.ajax.reload(null, false);
}