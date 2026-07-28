const messageElement = document.getElementById("message");

function setMessage(text, isError = false) {
    messageElement.textContent = text;
    messageElement.classList.toggle("error", isError);
}

function readForm(form) {
    return Object.fromEntries(
        [...new FormData(form).entries()]
            .map(([key, value]) => [key, value.trim()])
            .filter(([, value]) => value.length > 0)
    );
}

async function submitAuthForm(form, url) {
    setMessage("Отправка...");

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(readForm(form))
    });

    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.detail || error.message || "Ошибка запроса");
    }

    const auth = await response.json();
    localStorage.setItem("jwtToken", auth.token);
    localStorage.setItem("userEmail", auth.email);
    localStorage.setItem("userRole", auth.role);
    setMessage(`Успешно: ${auth.email}. JWT сохранён в localStorage.`);
}

document.getElementById("register-form")?.addEventListener("submit", async (event) => {
    event.preventDefault();

    try {
        await submitAuthForm(event.currentTarget, "/api/auth/register");
    } catch (error) {
        setMessage(error.message, true);
    }
});

document.getElementById("login-form")?.addEventListener("submit", async (event) => {
    event.preventDefault();

    try {
        await submitAuthForm(event.currentTarget, "/api/auth/login");
    } catch (error) {
        setMessage(error.message, true);
    }
});
