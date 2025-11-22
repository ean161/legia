var out;
var isFreezeAPI = false;
const urlPath = window.location.pathname;

const datatableLang = {
    search: "Tìm kiếm&ensp;",
    infoEmpty: "",
    emptyTable: "Không có dữ liệu",
    zeroRecords: "Không tìm thấy dữ liệu",
    info: "Hiển thị từ dòng _START_ đến _END_, tổng _TOTAL_ dòng",
    lengthMenu: ""
};

if (localStorage.getItem("theme_mode") === "dark")
    dark();
else
    light();

$(document).ready(function() {
    out = new Notyf();

    setInterval(async () => {
        await syncInfo();
    }, 10000);

    $("#side-menu-btn").on("click", () => {
        let sidebar = document.getElementById("side-menu").style.display;
        document.getElementById("side-menu").style.display = sidebar !== "block" ? "block" : "none";
    });

    browserBack(async () => {
        await syncInfo();
    });
});

window.addEventListener("message", function(event) {
    if (event.data.theme) {
        const theme = Array.from(htmlElement.classList).find((cls) => cls.startsWith("uk-theme-"), );
        if (theme) {
            htmlElement.classList.remove(theme);
        }

        htmlElement.classList.add(event.data.theme);
        localStorage.setItem("theme", event.data.theme);
    }
});

function browserBack(callback) {
    let last = Date.now();
    
    const sleepInterval = setInterval(() => {
        const now = Date.now();
        if (now - last > 5000) {
            callback("sleep_resume");
        }
        last = now;
    }, 1000);

    document.addEventListener("visibilitychange", () => {
        callback(document.hidden ? "tab_hidden" : "tab_visible");
    });

    window.addEventListener("blur", () => callback("blur"));
    window.addEventListener("focus", () => callback("focus"));

    return () => clearInterval(sleepInterval);
}

async function syncInfo() {
    if (urlPath === "/" || urlPath === "/auth") {
        return;
    }

    let me = await api("/auth/me", {});
    if (me !== undefined && me != null && "id" in me) {
        $("#balanceDisplay").html(`${me.balanceDisplay} vnd`);
    }
}

function light() {
    htmlElement.classList.remove("dark");
    localStorage.setItem("mode", "light");
    localStorage.setItem("theme_mode", "light");
}

function dark() {
    htmlElement.classList.add("dark");
    localStorage.setItem("mode", "dark");
    localStorage.setItem("theme_mode", "dark");
}

async function api(url, data = {}, type = "POST") {
    if (isFreezeAPI) {
        console.log("Cancelled req: ", url);
        return;
    }

    isFreezeAPI = true;
    try {
        const isFormData = data instanceof FormData;

        const res = await $.ajax({
            url: url,
            type: type,
            data: data,
            dataType: "json",
            processData: !isFormData,
            contentType: !isFormData ? "application/x-www-form-urlencoded; charset=UTF-8" : false,
        });

        isFreezeAPI = false;
        if ("message" in res) {
            if (res.message != null) {
                if (res.code === 0) {
                    out.error(res.message);
                } else if (res.code === 1) {
                    out.success(res.message);
                }
            }
        }
        
        if (res.data != null && res.code === 2) {
            window.location.href = res.data;
        }

        return res;
    } catch (jqXHR) {
        isFreezeAPI = false;

        if (jqXHR.status === 400) {
            out.error("Yêu cầu không hợp lệ");
        } else if (jqXHR.status === 401) {
            out.error("Chưa đăng nhập");
        } else {
            window.location.href = "/auth?type=login&r=" + urlPath;
        }

        return null;
    }
}

function numberFormat(input) {
    return parseInt(input).toLocaleString("vi-VN").replaceAll(".", ",");
}