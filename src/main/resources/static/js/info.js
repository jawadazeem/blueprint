(function () {
    const openDashboardBtn = document.getElementById("openDashboardBtn");

    if (openDashboardBtn) {
        openDashboardBtn.addEventListener("click", window.Blueprint.openDashboard);
    }
})();
