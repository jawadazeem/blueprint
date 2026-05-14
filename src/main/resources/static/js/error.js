(function () {
    const goHomeBtn = document.getElementById("goHomeBtn");

    if (goHomeBtn) {
        goHomeBtn.addEventListener("click", window.Blueprint.goHome);
    }
})();
