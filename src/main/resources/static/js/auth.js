function authByPhone(phone) {
    console.log("authStart:" + phone)
    let auth = sendPhone(phone)
        .then((phoneResult) => prompt('Enter login code', ''))
        .then((enteredLoginCode) => sendLoginCode(enteredLoginCode))
        .then((sendLoginCodeResult) => {
            console.log("end:" + sendLoginCodeResult.text())
        })

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

