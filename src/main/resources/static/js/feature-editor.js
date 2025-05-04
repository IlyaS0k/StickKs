class FeaturesContext {
    #currentFeature

    constructor() {
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

let context = new FeaturesContext()

function initialization() {
    context.currentFeature = null
    renderFeatures()
}

function renderFeatures() {
    const list = document.getElementById('featuresList');
    const filter = document.getElementById('filterInput').value.toLowerCase();
    list.innerHTML = '';

    features
        .filter(f => f.name.toLowerCase().includes(filter))
        .forEach(f => {
            const div = document.createElement('div');
            div.className = 'feature-item';
            div.innerText = `${f.name}`;
            div.onclick = () => loadFeature(f.id);
            list.appendChild(div);
        });
}

function loadFeature(id) {
    context.currentFeature = id
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
            alert(`Ошибка: ${response.status} — ${errorText}`)
        } else {
            const feature = await response.json()
            if (isNewFeature) {
                features.push({id: feature.id, name: feature.name, code: feature.code})
            } else {
                const index = features.findIndex(f => f.id === featureToSaveId)
                features[index].name = feature.name
                features[index].code = feature.code
            }
            renderFeatures()
            alert("Сохранено")
        }
    } catch (error) {
        alert("Ошибка: " + error.message)
    }
}

function clearEditor() {
    monacoEditor.setValue("");
}

function createNewFeature() {
    context.currentFeature = null
}

window.onload = initialization;