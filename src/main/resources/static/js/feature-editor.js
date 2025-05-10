class FeaturesContext {
    #currentFeature
    #features

    STABILITY_STABLE = "STABLE"
    STABILITY_UNSTABLE = "UNSTABLE"
    STABILITY_BROKEN = "BROKEN"

    FEATURE_UPDATE = "UPDATE"
    FEATURE_DELETE = "DELETE"
    FEATURE_CREATE = "CREATE"

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
            let featureCode = features.find(f => f.id === newCurrentFeature).code
            monacoEditor.setValue(featureCode)
        }
        this.#currentFeature = newCurrentFeature
    }

    addFeature(feature) {
        features.push(feature)
        renderFeature(feature.id, this.FEATURE_CREATE)
    }

    updateFeature(feature) {
        const index = features.findIndex(f => f.id === feature.id)
        features[index] = feature
        renderFeature(feature.id, this.FEATURE_UPDATE)
    }

    deleteFeature(feature) {
        const index = features.findIndex(f => f.id === feature.id)
        if (index !== -1) {
            features.splice(index, 1)
            renderFeature(feature.id, this.FEATURE_DELETE)
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

function initialization() {
    context.currentFeature = null
    renderFeatures()
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
    let f = context.features.find(f => f.id === id)
    const newDiv = createFeature(f)
    switch (op) {
        case context.FEATURE_CREATE:
            list.append(newDiv)
            break
        case context.FEATURE_UPDATE:
            const prevDiv = list.querySelector(`#${id}`)
            prevDiv.replaceWith(newDiv)
            break
        case context.FEATURE_DELETE:
            const deleteDiv = list.querySelector(`#${id}`)
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
    switch (f.stability) {
        case context.STABILITY_STABLE:
            div.innerText = `${f.name}`
            break
        case context.STABILITY_BROKEN:
            div.innerText = `[BROKEN] ${f.name}`
            div.style.color = "red"
            break
        case context.STABILITY_UNSTABLE:
            div.innerText = `[UNSTABLE] ${f.name}`
            div.style.color = "orange"
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
    console.log(id)
    try {
        if (confirm("Are you sure you want delete this feature?")) {
            const deleteResponse = await fetch("http://localhost:8080/features/delete", {
                method: "POST",
                body: JSON.stringify({id}),
                headers: {
                    "Content-type": "application/json; charset=utf-8"
                }
            })

            if (!deleteResponse.ok) {
                alert("Delete error: " + await deleteResponse.text());
            }
        }
    } catch (error) {
        alert("Delete error: " + error.message);
    }

}

async function saveFeature() {
    let featureToSaveId = context.currentFeature
    let isNewFeature = featureToSaveId == null
    const code = monacoEditor.getValue()
    if (!code) {
        alert("Code is empty!")
        return
    }
    try {
        const response = await fetch('http://localhost:8080/features/save', {
            method: "POST",
            body: JSON.stringify({
                id: featureToSaveId,
                code: code
            }),
            headers: {
                "Content-type": "application/json; charset=utf-8"
            }
        })

        if (!response.ok) {
            const errorText = await response.text()
            alert(`Error: ${response.status} — ${errorText}`)
        } else {
            const feature = await response.json()
            if (isNewFeature) {
                context.addFeature(feature)
            } else {
                context.updateFeature(feature)
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
            const logout = await fetch("http://localhost:8080/logout", {
                method: "POST",
                headers: {
                    "Content-type": "application/json; charset=utf-8"
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
    context.currentFeature = null
}

window.onload = initialization