$(document).ready(function() {
    $("#login-btn").on("click", () => {
        auth("login");
    });


    $("#register-btn").on("click", () => {
        auth("register");
    });

    $("#redirect").val((new URLSearchParams(window.location.search)).get("r"));
});

async function auth(type) {
    var res = await api("/auth", {
        type: type,
        username: $("#username").val(),
        password: $("#password").val(),
        redirect: $("#redirect").val()
    });

    if (res.code === 0) {
        return;
    }

    $("#password").val("");
}