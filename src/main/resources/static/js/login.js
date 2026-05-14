(function () {
    const form = document.getElementById("loginForm");
    const guestBtn = document.getElementById("guestBtn");
    const statusMessage = document.getElementById("statusMessage");

    function showMessage(type, text) {
        statusMessage.style.display = "block";
        statusMessage.className = "status-message " + type;
        statusMessage.textContent = text;
    }

    function enterApp(message) {
        showMessage("success", message);
        setTimeout(() => {
            window.location.href = "/";
        }, 600);
    }

    if (form) {
        form.addEventListener("submit", (e) => {
            e.preventDefault();

            const username = document.getElementById("username").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!username || !password) {
                showMessage("error", "Enter both username and password.");
                return;
            }

            console.log("Login:", { username, password });
            enterApp("Login accepted. Entering dashboard...");
        });
    }

    if (guestBtn) {
        guestBtn.addEventListener("click", () => {
            console.log("Guest access granted");
            enterApp("Entering as guest...");
        });
    }
})();
