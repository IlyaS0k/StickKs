require.config({ paths: { 'vs': 'https://cdn.jsdelivr.net/npm/monaco-editor@0.45.0/min/vs' } });
require(['vs/editor/editor.main'], function () {
    let monacoEditor = monaco.editor.create(document.getElementById('editor-container'), {
        value: "",
        language: 'kotlin',
        theme: 'vs-light',
        automaticLayout: true,
        minimap: {enabled: true},
        fontSize: 14,
        scrollBeyondLastLine: false
    });
})

let context = {
    userId: String
}
function initialization() {
    context.userId =
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
            div.onclick = () => loadFeature(f.code);
            list.appendChild(div);
        });
}

function loadFeature(code) {
    monacoEditor.setValue(code);
}

function saveFeature() {
    const code = monacoEditor.getValue().trim();
    if (!code) {
        alert("Code is empty!");
        return;
    }

    const match = code.match(/fun\s+(\w+)/);
    const featureName = match ? match[1] : "Unnamed";

    const existing = features.find(f => f.name === featureName);
    if (existing) {
        existing.code = code;
    } else {
        features.push({name: featureName, code: code});
    }

    renderFeatures();
    alert("Feature saved!");
}

function clearEditor() {
    monacoEditor.setValue("");
}

function createNewFeature() {
    monacoEditor.setValue("fun newFea() {\\n    // TODO: Implement\\n};")
}

window.onload = initialization;