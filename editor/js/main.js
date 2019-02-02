/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
let ATTRIBS = [
    'vitality',
    'spirit',
    'intelligence',
    'dexterity',
    'strength'
];

depend('filter');
depend('input');
depend('yaml');
depend('component', function() {
    const config = localStorage.getItem('config');
    if (config) {
        parseConfig(config);
    }
    refreshOptions();
});
depend('data/data', function () {
    depend('skill', function () {
        document.getElementById('skillList').addEventListener('change', function (e) {
            activeSkill.update();
            if (activeComponent) {
                activeComponent.update();
            }
            if (this.selectedIndex == this.length - 1) {
                newSkill();
            }
            else {
                activeSkill = skills[this.selectedIndex];
                activeSkill.apply();
                showSkillPage('builder');
            }
        });
        document.getElementById('skillDetails').addEventListener('click', function (e) {
            activeSkill.createFormHTML();
            showSkillPage('skillForm');
        });
        document.getElementById('saveButton').addEventListener('click', function (e) {
            saveToFile('skills.yml', getSkillSaveData());
        });
        document.getElementById('saveSkill').addEventListener('click', function (e) {
            saveToFile(activeSkill.data[0].value + '.yml', activeSkill.getSaveString());
        });
        document.getElementById('deleteSkill').addEventListener('click', function (e) {
            const list = document.getElementById('skillList');
            let index = list.selectedIndex;

            skills.splice(index, 1);
            if (skills.length == 0) {
                newSkill();
            }
            list.remove(index);
            index = Math.min(index, skills.length - 1);
            activeSkill = skills[index];
            list.selectedIndex = index;

            activeSkill.apply();
            showSkillPage('builder');
        });
    });

    depend('class', function () {
        document.getElementById('classList').addEventListener('change', function (e) {
            activeClass.update();
            if (this.selectedIndex == this.length - 1) {
                newClass();
            }
            else {
                activeClass = classes[this.selectedIndex];
                activeClass.createFormHTML();
            }
        });
        document.getElementById('saveButton').addEventListener('click', function (e) {
            saveToFile('classes.yml', getClassSaveData());
        });
    });

    document.getElementById("version-select").onchange = e => {
        DATA = window['DATA_' + e.target.value.substr(2)];
        localStorage.setItem('server-version', e.target.value);
    };

    const previousValue = localStorage.getItem('server-version');
    if (previousValue) {
        document.getElementById('version-select').value = previousValue;
    }
});

function getSkillSaveData() {
    activeSkill.update();
    if (activeComponent) {
        activeComponent.update();
    }
    let data = 'loaded: false\n';
    const alphabetic = skills.slice(0);
    alphabetic.sort(function (a, b) {
        const an = a.data[0].value;
        const bn = b.data[0].value;
        if (an > bn) return 1;
        if (an < bn) return -1;
        return 0;
    });
    for (let i = 0; i < alphabetic.length; i++) {
        data += alphabetic[i].getSaveString();
    }
    return data;
}

function getClassSaveData() {
    activeClass.update();
    let data = 'loaded: false\n';
    for (let i = 0; i < classes.length; i++) {
        data += classes[i].getSaveString();
    }
    return data;
}

function refreshOptions() {
    // Set up component option lists
    setupOptionList(document.getElementById('triggerOptions'), Trigger, Type.TRIGGER);
    setupOptionList(document.getElementById('targetOptions'), Target, Type.TARGET);
    setupOptionList(document.getElementById('conditionOptions'), Condition, Type.CONDITION);
    setupOptionList(document.getElementById('mechanicOptions'), Mechanic, Type.MECHANIC);
}

function setupOptionList(div, list, type) {
    div.innerHTML = '';
    let x;
    let output = '';
    const keys = Object.keys(list).sort();
    for (let i = 0; i < keys.length; i++) {
        x = keys[i];
        if (i % 4 == 0)
            output += '| ';
        output += '[[' + list[x].name + '|_' + type.substr(0, 1).toUpperCase() + type.substr(1) + ' ' + list[x].name + ']] | ';
        if ((i + 1) % 4 == 0)
            output += '\n';

        const e = document.createElement('h5');
        if (list[x].premium)
            e.className = 'premium';
        e.innerHTML = list[x].name;
        e.component = list[x];
        e.addEventListener('click', function (e) {
            if (activeComponent == activeSkill && activeSkill.usingTrigger(this.component.name)) {
                showSkillPage('builder');
            }
            else {
                showSkillPage('skillForm');
                const component = this.component.construct
                    ? new this.component.construct()
                    : this.component.supplier();
                component.parent = activeComponent;
                activeComponent.components.push(component);
                component.createBuilderHTML(activeComponent.html);
                component.createFormHTML();
            }
        });
        div.appendChild(e);
    }

    //saveToFile('wiki_' + type + '.txt', output);
}

let skillsActive = true;

// Set up event listeners when the page loads
window.onload = function () {
    const isOpera = !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
    const isFirefox = typeof InstallTrigger !== 'undefined';   // Firefox 1.0+
    const isChrome = !!window.chrome && !isOpera;              // Chrome 1+
    const badBrowser = !isOpera && !isFirefox && !isChrome;
    document.getElementById('badBrowser').style.display = badBrowser ? 'block' : 'none';
    if (badBrowser) {
        return;
    }

    document.getElementById('addTrigger').addEventListener('click', function (e) {
        activeComponent = activeSkill;
        showSkillPage('triggerChooser');
    });

    document.getElementById('skillTab').addEventListener('click', function (e) {
        switchToSkills();

    });
    document.getElementById('classTab').addEventListener('click', function (e) {
        switchToClasses();
    });

    const cancelButtons = document.querySelectorAll('.cancelButton');
    for (let i = 0; i < cancelButtons.length; i++) {
        cancelButtons[i].addEventListener('click', function (e) {
            showSkillPage('builder');
        });
    }

    const attribs = localStorage.getItem('attribs');
    const skillData = localStorage.getItem('skillData');
    const skillIndex = localStorage.getItem('skillIndex');
    const classData = localStorage.getItem('classData');
    const classIndex = localStorage.getItem('classIndex');
    if (attribs) {
        ATTRIBS = attribs.split(",");
    }
    if (skillData) {
        skills = [];
        document.getElementById('skillList').remove(0);
        loadSkillText(skillData);
        if (skillIndex) {
            document.getElementById('skillList').selectedIndex = parseInt(skillIndex);
            activeSkill = skills[Math.max(0, Math.min(skills.length - 1, parseInt(skillIndex)))];
            activeSkill.apply();
            showSkillPage('builder');
        }
    }
    if (classData) {
        classes = [];
        document.getElementById('classList').remove(0);
        loadClassText(classData);
        if (classIndex) {
            document.getElementById('classList').selectedIndex = parseInt(classIndex);
            activeClass = classes[Math.max(0, Math.min(classes.length - 1, parseInt(classIndex)))];
            activeClass.createFormHTML();
        }
    }
    if (localStorage.getItem('skillsActive') == 'false') {
        switchToClasses();
    }
}

function switchToSkills() {
    if (!skillsActive) {
        document.getElementById('skillTab').className = 'tab tabLeft tabActive';
        document.getElementById('classTab').className = 'tab tabRight';
        document.getElementById('skills').style.display = 'block';
        document.getElementById('classes').style.display = 'none';
        skillsActive = true;
    }
}

function switchToClasses() {
    if (skillsActive) {
        document.getElementById('classTab').className = 'tab tabRight tabActive';
        document.getElementById('skillTab').className = 'tab tabLeft';
        document.getElementById('classes').style.display = 'block';
        document.getElementById('skills').style.display = 'none';
        skillsActive = false;
    }
}

/**
 * Returns the view back to the skill builder when in the skill tab
 */
function showSkillPage(name) {
    setPageStyle('builder', name);
    setPageStyle('skillForm', name);
    setPageStyle('componentChooser', name);
    setPageStyle('triggerChooser', name);
}

/**
 * Sets the style for the page based on the current visible one
 */
function setPageStyle(name, visible) {
    document.getElementById(name).style.display = (visible == name ? 'block' : 'none');
}

/**
 * Represents an attribute of a skill or class
 *
 * @param {string} key   - the config key for the attribute
 * @param {double} base  - the starting value for the attribute
 * @param {double} scale - the increase of the value per level
 *
 * @constructor
 */
function Attribute(key, base, scale) {
    this.key = key;
    this.base = base;
    this.scale = scale;
}

/**
 * Saves text data to a file locally
 *
 * Code slightly modified from this page:
 * https://thiscouldbebetter.wordpress.com/2012/12/18/loading-editing-and-saving-a-text-file-in-html5-using-javascrip/
 */
function saveToFile(file, data) {
    const textFileAsBlob = new Blob([data], {type: 'text/plain;charset=utf-8'});

    const downloadLink = document.createElement("a");
    downloadLink.download = file;
    downloadLink.innerHTML = "Download File";
    if (window.webkitURL != null) {
        // Chrome allows the link to be clicked
        // without actually adding it to the DOM.
        downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob);
    }
    else {
        // Firefox requires the link to be added to the DOM
        // before it can be clicked.
        downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
        downloadLink.onclick = function (e) {
            document.body.removeChild(event.target);
        };
        downloadLink.style.display = "none";
        document.body.appendChild(downloadLink);
    }

    downloadLink.click();
}

// Prepares for handling dropped files
document.addEventListener('dragover', function (e) {
    e.stopPropagation();
    e.preventDefault();
    e.dataTransfer.dropEffect = 'copy';
}, false);

// Examines dropped files and sets up loading applicable ones
document.addEventListener('drop', function (e) {
    e.stopPropagation();
    e.preventDefault();
    for (let i = 0; i < e.dataTransfer.files.length; i++) {
        const file = e.dataTransfer.files[i];
        const reader = new FileReader();
        if (file.name == 'tool-config.json') {
            reader.onload = loadConfig;
        } else if (file.name.indexOf('.yml') == -1) {
            continue;
        } else if (file.name.indexOf('skills') == 0) {
            reader.onload = loadSkills;
        } else if (file.name.indexOf('classes') == 0) {
            reader.onload = loadClasses;
        } else {
            reader.onload = loadIndividual;
        }
        reader.readAsText(file);
    }
}, false);

function loadConfig(e) {
    const text = e.target.result;
    localStorage.setItem("config", text);
    parseConfig(text);
    refreshOptions();
}

function parseConfig(text) {
    const data = JSON.parse(text);
    const mapping = {
        CONDITION: Condition,
        MECHANIC: Mechanic,
        TARGET: Target,
        TRIGGER: Trigger
    };
    for (let i = 0; i < data.length; i++) {
        const entry = data[i];
        mapping[entry.type][entry.display.toUpperCase().replace(/ /g, '_')] = {
            name: entry.display,
            container: entry.container,
            supplier: function() {
                return new CustomComponent(entry);
            }
        }
    }
}

// Loads an individual skill or class file
function loadIndividual(e) {
    const text = e.target.result;
    if (text.indexOf('global:') >= 0) {
        loadAttributes(e);
    }
    else if (text.indexOf('components:') >= 0 || (text.indexOf('group:') == -1 && text.indexOf('combo:') == -1 && text.indexOf('skills:') == -1)) {
        loadSkills(e);
    }
    else {
        loadClasses(e);
    }
}

// Loads attribute data from a file
// e - event details
function loadAttributes(e) {
    const text = e.target.result;
    document.activeElement.blur();
    const yaml = parseYAML(text);
    ATTRIBS = Object.keys(yaml);
    if (!skillsActive) {
        activeClass.update();
        activeClass.createFormHTML();
    }
    localStorage.setItem('attribs', ATTRIBS);
}

// Loads skill data from a file after it has been read
// e - event details
function loadSkills(e) {
    const text = e.target.result;
    document.activeElement.blur();
    loadSkillText(text);
}

// Loads skill data from a string
function loadSkillText(text) {

    // Load new skills
    const data = parseYAML(text);
    for (let key in data) {
        if (data[key] instanceof YAMLObject && key != 'loaded') {
            if (isSkillNameTaken(key)) {
                getSkill(key).load(data[key]);
                if (getSkill(key) == activeSkill) {
                    activeSkill.apply();
                    showSkillPage('builder');
                }
            }
            else {
                addSkill(key).load(data[key]);
            }
        }
    }
}

// Loads class data from a file after it has been read
// e - event details
function loadClasses(e) {
    const text = e.target.result;
    document.activeElement.blur();
    loadClassText(text);
}

// Loads class data from a string
function loadClassText(text) {

    // Load new classes
    const data = parseYAML(text);
    for (let key in data) {
        if (data[key] instanceof YAMLObject && key != 'loaded' && !isClassNameTaken(key)) {
            if (isClassNameTaken(key)) {
                getClass(key).load(data[key]);
                if (getClass(key) == activeClass) {
                    activeClass.createFormHTML();
                }
            }
            else {
                addClass(key).load(data[key]);
            }
        }
    }
}

/**
 * Loads a section of config data
 */
function loadSection(data) {
    this.components = [];
    for (let x in data) {
        if (x == this.dataKey) {
            const attribs = data[x];
            for (var y in attribs) {
                for (var i = 0; i < this.data.length; i++) {
                    if (this.data[i].key == y && this.data[i].load) {
                        this.data[i].load(attribs[y]);
                        break;
                    }
                    else if (this.data[i].key + '-base' == y && this.data[i].loadBase) {
                        this.data[i].loadBase(attribs[y]);
                        break;
                    }
                    else if (this.data[i].key + '-scale' == y && this.data[i].loadScale) {
                        this.data[i].loadScale(attribs[y]);
                        break;
                    }
                }
            }
        }
        else if (x == this.componentKey) {
            const components = data[x];
            for (var y in components) {
                const type = components[y].type;
                let list;
                if (type == Type.TRIGGER) {
                    list = Trigger;
                }
                else if (type == Type.TARGET) {
                    list = Target;
                }
                else if (type == Type.CONDITION) {
                    list = Condition;
                }
                else if (type == Type.MECHANIC) {
                    list = Mechanic;
                }

                let key = y;
                if (key.indexOf('-') > 0) key = key.substring(0, key.indexOf('-'));
                if (list !== undefined) {
                    for (let z in list) {
                        if (list[z].name.toLowerCase() == key.toLowerCase()) {
                            const component = list[z].construct
                                ? new list[z].construct()
                                : list[z].supplier();
                            component.parent = this;
                            this.components.push(component);
                            component.load(components[y]);
                        }
                    }
                }
            }
        }
        else if (this.dataKey != 'data') {
            for (var i = 0; i < this.data.length; i++) {
                if (this.data[i].key == x) {
                    if (!this.data[i].load) {
                        debugger;
                    }
                    this.data[i].load(data[x]);
                    break;
                }
                else if (this.data[i].key + '-base' == x) {
                    this.data[i].loadBase(data[x]);
                    break;
                }
                else if (this.data[i].key + '-scale' == x) {
                    this.data[i].loadScale(data[x]);
                    break;
                }
            }
        }
    }
}

/**
 * Remember the current session data for next time
 */
window.onbeforeunload = function () {
    localStorage.setItem('skillData', getSkillSaveData());
    localStorage.setItem('classData', getClassSaveData());
    localStorage.setItem('skillsActive', this.skillsActive ? 'true' : 'false');
    localStorage.setItem('skillIndex', document.getElementById('skillList').selectedIndex);
    localStorage.setItem('classIndex', document.getElementById('classList').selectedIndex);
};