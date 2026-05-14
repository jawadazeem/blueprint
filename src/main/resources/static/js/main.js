(function () {
    window.Blueprint = window.Blueprint || {};

    window.Blueprint.goHome = function () {
        window.location.href = "/";
    };

    window.Blueprint.openDashboard = function () {
        window.open("/", "_self");
    };

    window.Blueprint.openInfoPopup = function () {
        window.open(
            "/info.html",
            "infoPopup",
            "width=600,height=650,resizable=yes,scrollbars=yes"
        );
    };
})();
