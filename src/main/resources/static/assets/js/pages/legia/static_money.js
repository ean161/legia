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

            let url = `/legia/static-money/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "note", orderable: false },
            { data: "amountDisplay", orderable: false },
            { data: "createdDateDisplay", orderable: false },
            {
                data: "actions",
                title: "Thao tác",
                orderable: false,
                render: function (data, type, row) {
                    // if (!row.allowEdit) {
                    //     return "-";
                    // }

                    return `<button class="uk-button uk-button-secondary" onclick="editMoney(${row.id}, '${row.staffName}', '${row.note}', '${row.amount}')">Sửa</button>`;
                }
            }
        ]
    });

    $("#edit-money-submit").on("click", async () => {
        let res = await api("/legia/hrm/edit-static-money", {
            id: localStorage.getItem("id"),
            note: $("#enote").val(),
            type: $("#etype").val(),
            amount: $("#eamount").val()
        });

        if (res.code === 0) {
            return;
        }

        UIkit.modal("#edit-money").toggle();

        $("#eid").val("");
        $("#ename").val("");
        $("#etype").val("");
        $("#eamount").val(0);

        list.ajax.reload(null, false);
    });
});

function editMoney(id, name, note, amount) {
    UIkit.modal("#edit-money").toggle();

    localStorage.setItem("id", id);

    $("#ename").val(name);
    $("#enote").val(note);
    $("#eamount").val(amount.replace("-", ""));
    $("#etype").val(amount < 0 ? "SUB" : "PLUS");
}