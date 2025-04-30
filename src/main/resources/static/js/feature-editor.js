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
    document.getElementById('codeEditor').value = code;
}

function saveFeature() {
    const code = document.getElementById('codeEditor').value.trim();
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
        features.push({ name: featureName, code: code });
    }

    renderFeatures();
    alert("Feature saved!");
}

function clearEditor() {
    document.getElementById('codeEditor').value = '';
}

function createNewFeature() {
    document.getElementById('codeEditor').value = 'fun newFea() {\n    // TODO: Implement\n}';
}

window.onload = renderFeatures;