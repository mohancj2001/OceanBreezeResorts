async function forgetpassword() {
    try {
        // Get input values
        const code = document.getElementById("code").value.trim();
        const password = document.getElementById("password").value.trim();

        // Validate inputs
        if (!code || !password) {
            alert("Please enter both verification code and new password.");
            return;
        }

        const dto = { code, password };

        const response = await fetch("AdminForgetpassword", {
            method: "POST",
            body: JSON.stringify(dto),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("Server responded with an error.");
        }

        const json = await response.json();

        if (json.status) { 
            alert("Password changed successfully! Redirecting to login...");
            window.location.href = "login.html"; 
        } else {
            alert(json.message || "Password reset failed. Please check your code and try again.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An unexpected error occurred. Please try again later.");
    }
}
