async function authByPhone(phone) {
    console.log("authStart:" + phone)

    try {
        const phoneResponse = await sendPhone(phone)
        if (!phoneResponse.ok) {
            alert("Authorization error: " + await phoneResponse.text())
            return
        }
        const enteredLoginCode = prompt('Enter login code', '')
        const loginResponse = await sendLoginCode(enteredLoginCode)
        if (!loginResponse.ok) {
            alert("Authorization error: " + await loginResponse.text())
        }
    } catch (error) {
        alert("Authorization error: " + error.message);
    }
}

function sendPhone(phone) {
    return fetch("http://localhost:8080/auth/submit-phone", {
        method: "POST",
        body: phone,
        headers: {
            "Content-type": "application/json; charset=utf-8"
        }
    })
}

function sendLoginCode(loginCode) {
    return fetch("http://localhost:8080/auth/submit-login-code", {
        method: "POST",
        body: loginCode,
        headers: {
            "Content-type": "application/json; charset=utf-8"
        }
    })
}

