async function loginByPhone(phone) {
    try {
        const phoneResponse = await sendPhone(phone)
        if (!phoneResponse.ok) {
            alert("Sending phone error: " + await phoneResponse.text())
            return
        }
        const {value: code} = await Swal.fire({
            title: 'Enter login code',
            input: 'text',
            inputLabel: 'Login code',
            inputPlaceholder: '123456',
            showCancelButton: true,
            customClass: {
                popup: 'login-swal-popup'
            }
        })

        if (code) {
            const loginResponse = await sendLoginCode(code)

            if (!loginResponse.ok) {
                alert("Error confirming phone number: " + await loginResponse.text())
            } else {
                window.location.href = "/features"
            }
        }
    } catch (error) {
        alert("Error confirming login code: " + error.message);
    }
}

function sendPhone(phone) {
    return fetch("http://localhost:8080/login/submit-phone", {
        method: "POST",
        body: phone,
        headers: {
            "Content-type": "application/json; charset=utf-8"
        }
    })
}

function sendLoginCode(loginCode) {
    return fetch("http://localhost:8080/login/submit-login-code", {
        method: "POST",
        body: loginCode,
        headers: {
            "Content-type": "application/json; charset=utf-8"
        }
    })
}

