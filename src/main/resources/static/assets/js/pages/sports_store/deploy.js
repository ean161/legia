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

            let url = `/sports-store/deploy-history?page=${page}&size=${size}&sort=${sortCol},${sortDir}`;
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
            { data: "fileSize", orderable: false },
            { data: "status", orderable: false },
            { data: "createdDateDisplay", orderable: false }
        ]
    });

    $("#deploy-btn").on("click", () => {
        UIkit.modal("#deploy").toggle();
    });

    $("#deploy-submit").on("click", async () => {
        const fileInput = document.getElementById("deploy-jar");
        if (!fileInput.files.length) {
            console.log("Please choose a file first!");
            return;
        }

        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        console.log(formData);

        let res = await api("/sports-store/deploy", formData);

        console.log(res);
        if (res.code == 0) {
            return;
        }

        UIkit.modal("#deploy").toggle();

        // $("#directory").val("");
        // $("#title").val("");
        // $("#note").val("");
        // $("#icon").val("");
        // $("#expiredDate").val("");

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