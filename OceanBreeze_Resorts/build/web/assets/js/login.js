async function login() {
    const admin_dto = {
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
    }

    console.log(admin_dto);

    const response = await fetch(
        "AdminSignIn",
        {
            method: "POST",
            body: JSON.stringify(admin_dto),
            headers: {
                "Content-Type": "application/json"
            }
        }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.success) {
            window.location = "index.html";
        }
    } else {
        alert("Please Try Again Later!!!!");
    }
}

async function sendEmail() {
    try {
        const dto = {
            email: document.getElementById("email").value,
        };

        const response = await fetch("VerifyAdmin", {
            method: "POST",
            body: JSON.stringify(dto),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            const json = await response.json();

            if (json.status) { 
                window.location.href = "forgotpassword.html"; 
            } else {
                alert(json.message);
            }
        } else {
            alert("Error occurred. Please try again later.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An unexpected error occurred. Please check your internet connection and try again.");
    }
}

