(() => {
    const form = document.getElementById("loginForm") as HTMLFormElement | null;
    const guestBtn = document.getElementById("guestBtn") as HTMLButtonElement | null;
    const statusMessage = document.getElementById("statusMessage") as HTMLDivElement | null;

    function showMessage(type: "error" | "success", text: string): void {
        if (!statusMessage) return;

        statusMessage.style.display = "block";
        statusMessage.className = "status-message " + type;
        statusMessage.textContent = text;
    }

    function enterApp(message: string): void {
        showMessage("success", message);

        setTimeout(() => {
            window.location.href = "/";
        }, 600);
    }

    if (form) {
        form.addEventListener("submit", (e: SubmitEvent) => {
            e.preventDefault();

            const usernameInput = document.getElementById("username") as HTMLInputElement | null;
            const passwordInput = document.getElementById("password") as HTMLInputElement | null;

            const username = usernameInput?.value.trim() ?? "";
            const password = passwordInput?.value.trim() ?? "";

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