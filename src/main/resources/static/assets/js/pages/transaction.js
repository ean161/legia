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

            let url = `/transaction/list?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "serviceDisplay", orderable: false },
            { data: "amountDisplay", orderable: false },
            { data: "note", orderable: false },
            {
                data: "status",
                orderable: false,
                render: function (data, type, row) {
                    switch (data) {
                        case "fail":
                            return `<span class="uk-text-danger">Thất bại</span>`;
                        case "wait":
                            return `<span class="uk-text-warning">Chờ xử lý</span>`;
                        case "success":
                            return `<span class="uk-text-success">Thành công</span>`;
                        default:
                            return data;
                    }
                }
            },
            { data: "createdDateDisplay", orderable: false }
        ]
    });

    $("#banking-deposit-btn").on("click", () => {
        UIkit.modal("#banking-deposit").toggle();
    });

    $("#withdraw-btn").on("click", () => {
        UIkit.modal("#withdraw").toggle();
    });

    $("#method").on("change", () => {
        var method = $("#method").val();
        $("#banking-form").hide();
        $("#amount-form").hide();

        if (method == "banking" || method == "card") {
            $("#amount-form").show();
        }

        if (method == "banking") {
            $("#banking-form").show();
        } else if (method == "card") {
            $("#card-form").show();
        }
    });

    $("#withdraw-submit").on("click", async () => {
        var method = $("#method").val();
        var res;

        if (method == "banking") {
            res = await api("/transaction/banking-withdraw", {
                bankType: $("#bankType").val(),
                bankNum: $("#bankNum").val(),
                amount: $("#amount").val()
            });

            if (res.code == 0) {
                return;
            }

            $("#bankType").val("");
            $("#bankNum").val("");
        } else if (method == "card") {
            res = await api("/transaction/card-withdraw", {
                cardType: $("#cardType").val(),
                amount: $("#amount").val()
            });

            if (res.code == 0) {
                return;
            }

            $("#cardType").val("");
        } else {
            out.error("Phương thức rút không hợp lệ");
        }

        $("#method").val("");
        $("#amount").val("");
        UIkit.modal("#withdraw").toggle();

        list.ajax.reload(null, false);
    });
});