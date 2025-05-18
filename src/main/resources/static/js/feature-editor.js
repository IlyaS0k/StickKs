class FeaturesContext {
    #currentFeature
    newFeatureSelections = 0
    #features
    wsEventListeners = new Set()
    wsMessageBuffer = []
    APP_ADDRESS = "localhost:8080"

    STATUS_STABLE = "STABLE"
    STATUS_UNSTABLE = "UNSTABLE"
    STATUS_BROKEN = "BROKEN"
    STATUS_LOADING = "LOADING"
    STATUS_LOADING_UNSTABLE = "LOADING_UNSTABLE"
    STATUS_CREATING = "CREATING"
    STATUS_UPDATING = "UPDATING"
    STATUS_DELETING = "DELETING"

    FEATURE_UPDATE = "UPDATE"
    FEATURE_DELETE = "DELETE"
    FEATURE_CREATE = "CREATE"

    NOTIFICATION_FEATURE_LOADED = "FEATURE_LOADED"
    NOTIFICATION_FEATURE_DELETED = "FEATURE_DELETED"
    NOTIFICATION_FEATURE_CREATED = "FEATURE_CREATED"
    NOTIFICATION_FEATURE_UPDATED = "FEATURE_UPDATED"
    NOTIFICATION_FEATURE_UNSTABLE = "FEATURE_UNSTABLE"

    constructor(features) {
        this.#features = features
    }

    get features() {
        return this.#features
    }

    get currentFeature() {
        return this.#currentFeature
    }

    set currentFeature(newCurrentFeature) {
        if (newCurrentFeature == null) {
            monacoEditor.setValue("feature {\n\n}")
        } else {
            let featureCode = this.#features.find(f => f.id === newCurrentFeature).code
            monacoEditor.setValue(featureCode)
        }
        this.#currentFeature = newCurrentFeature
    }

    addFeature(feature) {
        this.#features.push(feature)
        renderFeature(feature.id, this.FEATURE_CREATE)
    }

    updateFeature(feature) {
        const index = this.#features.findIndex(f => f.id === feature.id)
        this.#features[index] = feature
        renderFeature(feature.id, this.FEATURE_UPDATE)
    }

    updateFeatureStatus(id, status) {
        const feature = this.#features.find(f => f.id === id)
        if (feature != null) {
            feature.status = status
            renderFeature(feature.id, this.FEATURE_UPDATE)
        }
    }

    deleteFeature(id) {
        console.log(`deleting feature with id ${id}`)
        const index = this.#features.findIndex(f => f.id === id)
        if (index !== -1) {
            this.#features.splice(index, 1)
            renderFeature(id, this.FEATURE_DELETE)
        }
    }
}

let monacoEditor;

require.config({paths: {'vs': 'https://cdn.jsdelivr.net/npm/monaco-editor@0.45.0/min/vs'}});

require(['vs/editor/editor.main'], function () {
    monacoEditor = monaco.editor.create(document.getElementById('editor-container'), {
        value: "",
        language: 'kotlin',
        theme: 'vs-light',
        automaticLayout: true,
        minimap: {enabled: true},
        fontSize: 14,
        scrollBeyondLastLine: false
    });
})

let context = new FeaturesContext(features)


async function initialization() {
    context.currentFeature = null
    renderFeatures()
    let ws = new WebSocket(`ws://${context.APP_ADDRESS}/ws`)
    ws.addEventListener("message", event => {
        try {
            const message = JSON.parse(event.data)
            console.log("RECEIVED NOTIFICATION MESSAGE: \n" + JSON.stringify(message, null, 2))
            if (message.type === context.NOTIFICATION_FEATURE_UNSTABLE) {
                context.updateFeatureStatus(message.id, context.STATUS_UNSTABLE)
                return
            }
            let handled = false
            for (const listener of context.wsEventListeners) {
                if (listener(message) === true) {
                    handled = true;
                }
            }

            if (!handled) {
                context.wsMessageBuffer.push(message)
            }
        } catch (e) {
            console.error("Failed to parse message:", e)
        }
    })
    ws.addEventListener("open", () => {
        console.log("WebSocket connection established")
    })
    ws.addEventListener("close", () => {
        console.error("WebSocket closed")
    })
    for (const f of context.features.filter(f => f.status === context.STATUS_LOADING || f.status === context.STATUS_LOADING_UNSTABLE)) {
        waitWsEvent(null, f.id, context.NOTIFICATION_FEATURE_LOADED)
            .then(() => {
                context.updateFeatureStatus(f.id, f.status === context.STATUS_LOADING ? context.STATUS_STABLE : context.STATUS_UNSTABLE)
            })
            .catch((e) => {
                console.warn(`Timeout or error for feature ${f.id}:`, e);
            });
    }
}

function onMessage(callback) {
    context.wsEventListeners.add(callback);
    return () => context.wsEventListeners.delete(callback);
}


function waitWsEvent(reqId, featureId, eventType, timeoutMs = 15000) {
    return new Promise((resolve, reject) => {
        const index = context.wsMessageBuffer.findIndex(
            msg => (eventType === null || msg.type === eventType) &&
                (featureId === null || msg.id === featureId) &&
                (reqId === null || msg.reqId === reqId)
        )

        if (index !== -1) {
            const msg = context.wsMessageBuffer[index]
            context.wsMessageBuffer.splice(index, 1)
            return resolve(msg)
        }
        const timeout = setTimeout(() => {
            unsubscribe();
            reject(new Error("Timeout waiting for event"));
        }, timeoutMs);

        const unsubscribe = onMessage(msg => {
            if (reqId !== null && reqId !== msg.reqId) return false
            if (featureId !== null && featureId !== msg.id) return false
            if (eventType !== null && eventType !== msg.type) return false
            clearTimeout(timeout);
            unsubscribe();
            resolve(msg);
            return true
        });
    });
}

function renderFeatures() {
    let filter = document.getElementById('filterInput').value.toLowerCase()
    let list = document.getElementById('featuresList')
    list.innerHTML = ''

    context.features
        .filter(f => f.name.toLowerCase().includes(filter))
        .forEach(f => {
            const featureDiv = createFeature(f)
            list.append(featureDiv)
        })
}

function renderFeature(id, op) {
    let list = document.getElementById('featuresList')
    let f
    let newDiv
    switch (op) {
        case context.FEATURE_CREATE:
            f = context.features.find(f => f.id === id)
            newDiv = createFeature(f)
            list.append(newDiv)
            break
        case context.FEATURE_UPDATE:
            f = context.features.find(f => f.id === id)
            newDiv = createFeature(f)
            const prevDiv = list.querySelector(`#${CSS.escape(id)}`)
            prevDiv.replaceWith(newDiv)
            break
        case context.FEATURE_DELETE:
            const deleteDiv = list.querySelector(`#${CSS.escape(id)}`)
            deleteDiv.replaceWith('')
            break
        default:
            break
    }
}

function createFeature(f) {
    const div = document.createElement('div');
    div.className = 'feature-item';
    div.id = f.id
    switch (f.status) {
        case context.STATUS_STABLE:
            div.innerText = `${f.name}`
            break
        case context.STATUS_BROKEN:
            div.innerText = `[BROKEN] ${f.name}`
            div.style.color = "red"
            break
        case context.STATUS_UNSTABLE:
            div.innerText = `[UNSTABLE] ${f.name}`
            div.style.color = "orange"
            break
        case context.STATUS_LOADING:
        case context.STATUS_LOADING_UNSTABLE:
        case context.STATUS_CREATING:
        case context.STATUS_UPDATING:
        case context.STATUS_DELETING:
            div.innerText = `[${f.status}...] ${f.name}`
            div.style.color = "gray"
            break
        default:
    }
    div.onclick = () => loadFeature(f.id);
    return div
}

function loadFeature(id) {
    context.currentFeature = id
}


async function deleteFeature() {
    const id = context.currentFeature
    if (id === null) {
        alert("Attempt to delete unsaved feature")
        return
    }
    try {
        if (confirm("Are you sure you want delete this feature?")) {
            const reqId = crypto.randomUUID()
            const deleteResponse = await fetch(`http://${context.APP_ADDRESS}/features/delete/${id}`, {
                method: "POST",
                headers: {
                    "Content-type": "application/json; charset=utf-8",
                    "X-Request-ID": reqId
                }
            })

            if (!deleteResponse.ok) {
                alert("Delete error: " + await deleteResponse.text());
            } else {
                context.updateFeatureStatus(id, context.STATUS_DELETING)
                await waitWsEvent(reqId, null, context.NOTIFICATION_FEATURE_DELETED)
                context.deleteFeature(id)
                if (context.currentFeature === id) {
                    createNewFeature()
                }
            }
        }
    } catch (error) {
        alert("Delete error: " + error.message);
    }
}

async function showErrors() {
    const id = context.currentFeature
    if (id === null) {
        alert("No errors for unsaved feature")
        return
    }
    try {
        window.location.href = `http://${context.APP_ADDRESS}/features/errors?id=${id}`
    } catch (error) {
        alert("Failed to show errors: " + error.message);
    }
}

async function saveFeature() {
    let featureToSaveId = context.currentFeature
    let isNewFeature = featureToSaveId == null
    let oldnewFeatureSelections = context.newFeatureSelections
    const code = monacoEditor.getValue()
    if (!code) {
        alert("Code is empty!")
        return
    }
    try {
        let reqId = crypto.randomUUID()
        const response = await fetch(`http://${context.APP_ADDRESS}/features/save`, {
            method: "POST",
            body: JSON.stringify({
                id: featureToSaveId,
                code: code
            }),
            headers: {
                "Content-type": "application/json; charset=utf-8",
                "X-Request-ID": reqId
            }
        })

        if (!response.ok) {
            const errorText = await response.text()
            alert(`Error: ${response.status} — ${errorText}`)
        } else {
            const feature = await response.json()
            if (isNewFeature) {
                context.addFeature(feature)
                if (context.currentFeature == null && oldnewFeatureSelections === context.newFeatureSelections) {
                    context.currentFeature = feature.id
                }
                await waitWsEvent(reqId, null, context.NOTIFICATION_FEATURE_CREATED)
                context.updateFeatureStatus(feature.id, context.STATUS_STABLE)
            } else {
                context.updateFeature(feature)
                await waitWsEvent(reqId, null, context.NOTIFICATION_FEATURE_UPDATED)
                context.updateFeatureStatus(feature.id, context.STATUS_STABLE)
            }
            alert("Saved")
        }
    } catch (error) {
        alert("Error: " + error.message)
    }
}

async function logout() {
    try {
        if (confirm("Are you sure you want to log out?")) {
            const logout = await fetch(`http://${context.APP_ADDRESS}/logout`, {
                method: "POST",
                headers: {
                    "Content-type": "application/json; charset=utf-8",
                    "X-Request-ID": crypto.randomUUID()
                }
            })

            if (!logout.ok) {
                alert("Logout error: " + await logout.text());
                return
            }

            window.location.href = "/login"
        }
    } catch (error) {
        alert("Logout error: " + error.message);
    }
}

function createNewFeature() {
    context.newFeatureSelections++
    context.currentFeature = null
}

window.onload = initialization