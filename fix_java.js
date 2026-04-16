const fs = require('fs');

const apiFile = fs.readFileSync('api/info.js', 'utf8');

// Extract the db object
const match = apiFile.match(/const db = (\{[\s\S]*?\n    \};\n)/);
if (!match) {
    console.error("Could not find db in api/info.js");
    process.exit(1);
}

const dbStr = match[1];
// Evaluate safely
let db;
eval(`db = ${dbStr}`);

let javaLines = [];
javaLines.push('        public ApiHandler() {');

for (const topicId of Object.keys(db)) {
    javaLines.push(`            // Topic ${topicId}`);
    const langs = db[topicId];
    for (const lang of Object.keys(langs)) {
        const info = langs[lang];
        const t = info.title ? info.title.replace(/"/g, '\\"').replace(/\n/g, '\\n') : '';
        const m = info.text ? info.text.replace(/"/g, '\\"').replace(/\n/g, '\\n') : '';
        const img = info.imageUrl ? `"${info.imageUrl}"` : 'null';
        const spk = info.spokenText ? `"${info.spokenText.replace(/"/g, '\\"').replace(/\n/g, '\\n')}"` : null;
        
        if (spk) {
            javaLines.push(`            addTopic("${topicId}", "${lang}", "${t}",\n                    "${m}",\n                    ${img},\n                    ${spk});`);
        } else {
            javaLines.push(`            addTopic("${topicId}", "${lang}", "${t}",\n                    "${m}",\n                    ${img});`);
        }
    }
    javaLines.push('');
}
javaLines.push('        }');

const serverFile = fs.readFileSync('Server.java', 'utf8');

const regex = /public ApiHandler\(\) \{[\s\S]*?\}\n\n                private void addTopic/m;

if (regex.test(serverFile)) {
    const newServerFile = serverFile.replace(regex, javaLines.join('\n') + '\n\n                private void addTopic');
    fs.writeFileSync('Server.java', newServerFile, 'utf8');
    console.log("Server.java fixed successfully!");
} else {
    console.error("Regex did not match Server.java constructor");
}
