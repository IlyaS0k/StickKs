function escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function highlightMatch(text, query) {
    if (!query) return text;
    const pattern = new RegExp(`(${escapeRegExp(query)})`, 'gi');
    return text.replace(pattern, '<span class="highlight">$1</span>');
}

function createLogElement(log, query = '') {
    const wrapper = document.createElement('div');
    wrapper.className = 'log-entry';

    const header = document.createElement('div');
    header.className = 'log-header';

    const meta = document.createElement('div');
    meta.className = 'log-meta';
    meta.innerHTML = `<strong>Log ID:</strong> ${log.id} &nbsp;&nbsp; <strong>Timestamp:</strong> ${new Date(log.timestamp).toLocaleString()}`;

    const button = document.createElement('button');
    button.className = 'toggle-btn';
    button.textContent = 'Show';

    const trace = document.createElement('div');
    trace.className = 'stacktrace';
    trace.innerHTML = highlightMatch(log.trace, query);

    button.addEventListener('click', () => {
        trace.classList.toggle('expanded');
        button.textContent = trace.classList.contains('expanded') ? 'Hide' : 'Show';
    });

    header.appendChild(meta);
    header.appendChild(button);

    wrapper.appendChild(header);
    wrapper.appendChild(trace);

    return wrapper;
}

function renderLogs(query = '') {
    const container = document.getElementById('log-container');
    container.innerHTML = '';
    logs.forEach(log => {
        const match = !query || log.trace.toLowerCase().includes(query.toLowerCase());
        if (match) {
            container.appendChild(createLogElement(log, query));
        }
    });
}

function filterLogs() {
    const filter = document.getElementById('filter-input').value.trim();
    renderLogs(filter);
}

renderLogs();