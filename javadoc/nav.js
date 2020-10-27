'use strict';

var packageIndex = PACKAGES.length;
var classIndex = -1;
var targetElement = window.location.href.split('?')[0].split('#')[1];
var url = window.location.href.split('?')[0].split('#')[0];
var py, cy;

// Initializes page elements when the page loads
window.onload = function () {
    try {
        getIndices();
    }
    catch (err) { }
    setupSideNavigation();
    setupContent();
    scrollToElement(targetElement);
}

// Gets the package and class indices from the window location
function getIndices() {
    if (window.location.href.indexOf('?') < 0) return;
    var page = window.location.href.split('?')[1];
    if (page.indexOf(';') > 0) {
        var split = page.split(';');
        page = split[1];
        split = split[0].split(':');
        py = parseInt(split[0]);
        cy = parseInt(split[1]);
        setTimeout(function () {
            document.querySelector('#packageList').scrollTop = py;
            document.querySelector('#classList').scrollTop = cy;
            console.log(py + ", " + cy + " -> "
            + document.querySelector('#packageList').scrollTop + ", "
            + document.querySelector('#classList').scrollTop);
        }, 1);
        
    }
    if (page != '*') {
        var parts = page.split(':');
        var i;
        for (i = 0; i < PACKAGES.length; i++) {
            if (PACKAGES[i] == parts[0]) {
                packageIndex = i;
                break;
            }
        }
        if (parts[1]) {
            classIndex = getClassId(parts[1]);
        }
    }
}

function linkElement(name, id) {
    if (!id) id = name;
    var a = document.createElement('a');
    a.addEventListener('click', elementClicked);
    a['eId'] = id;
    a.innerHTML = name;
    return a;
}

function elementClicked() {
    window.location.href = url + '#' + this.eId + '?' + getScrollData() + PACKAGES[packageIndex] + ':' + getClass(classIndex).name;
}

function scrollToElement(element) {
    var target = document.querySelector('#' + element);
    if (target) {
        document.querySelector('#main').scrollTop = target.offsetTop;
    }
}

// Links the class to it's URL
function linkClass(name) {
    var lt = name.indexOf('<');
    if (lt > 0) {
        var container = document.createElement('span');
        container.appendChild(linkClass(name.substr(0, lt)));
        container.appendChild(linkClass("<"));
        var comma = name.indexOf(",");
        if (comma > lt) {
            container.appendChild(linkClass(name.substring(lt + 1, comma)));
            container.appendChild(linkClass(", "));
            container.appendChild(linkClass(name.substring(comma + 1, name.length - 1)));
        }
        else container.appendChild(linkClass(name.substring(lt + 1, name.length - 1)));
        container.appendChild(linkClass(">"));
        return container;
    }
    var link = linkClassList(name, INTERFACES);
    if (!link) link = linkClassList(name, CLASSES);
    if (!link) link = linkClassList(name, ENUMS);
    if (!link) link = linkClassList(name, ANNOTATIONS);
    if (!link) link = linkClassList(name, EXCEPTIONS);
    if (link) return link;
    var e = document.createElement('span');
    e.innerHTML = name;
    return e;
}

// Searches through a list to find the URL of the class
function linkClassList(name, list) {
    for (var i = 0; i < list.length; i++) {
        var subList = list[i];
        for (var j = 0; j < subList.length; j++) {
            if (subList[j].name == name) {
                var l = document.createElement('a');
                l.addEventListener('click', linkClick);
                l['linkKey'] = PACKAGES[i] + ':' + name;
                l.innerHTML = name;
                return l;
            }
        }
    }
    return false;
}

function linkClick() {
    window.location.href = url + '?' + getScrollData() + this.linkKey;
}

// Sets up the sidebar navigation
function setupSideNavigation() {
    if (PACKAGES.length == 0) return;
    var pList = document.querySelector('#packageList');
    var allLink = document.createElement('a');
    allLink.innerHTML = 'All Classes';
    allLink.className = 'packageLink';
    allLink.packageIndex = PACKAGES.length;
    if (packageIndex == PACKAGES.length) allLink.className += ' selectedPackage';
    allLink.onclick = linkClicked;
    pList.appendChild(allLink);
    addTag(pList, 'p', 'Packages');
    for (var i = 0; i < PACKAGES.length; i++) {
        var link = document.createElement('a');
        link.innerHTML = PACKAGES[i];
        link.className = 'packageLink';
        link.packageIndex = i
        if (i == packageIndex) link.className += ' selectedPackage';
        link.onclick = linkClicked;
        pList.appendChild(link);
    }

    updateClasses();
}

function linkClicked() {
    window.location.href = url + '?' + getScrollData() +
        (this.innerHTML == 'All Classes' ? '*'
        : this.className.charAt(0) != 'c' ? PACKAGES[this.packageIndex]
        : packageIndex == PACKAGES.length ? this.package + ':' + this.innerHTML
        : PACKAGES[packageIndex] + ':' + this.innerHTML);
}

function getScrollData() {
    return document.querySelector('#packageList').scrollTop + ':' 
        + document.querySelector('#classList').scrollTop + ';';
}

// Sets up the content in the center panel
function setupContent() {
    if (packageIndex == PACKAGES.length) setupOverview();
    else if (classIndex == -1) setupPackage();
    else setupClass();
}

// Sets up the content for the overview
function setupOverview() {
    var content = getMainSection();
    
    // General data
    if (NAME) addTag(content, 'h1', NAME);
    if (DESCRIPTION) addTag(content, 'p', DESCRIPTION);

    // Authors
    var row;
    if (AUTHORS.length > 0) {
        addTag(content, 'header', 'Authors');
        var table = addTag(content, 'table');
        for (var i = 0; i < AUTHORS.length; i++) {
            row = addTag(table, 'tr');
            addTag(row, 'td', AUTHORS[i]);
        }
    }

    // Depends
    var row;
    if (DEPENDS.length > 0) {
        addTag(content, 'header', 'Dependencies');
        var table = addTag(content, 'table');
        for (var i = 0; i < DEPENDS.length; i++) {
            row = addTag(table, 'tr');
            addTag(row, 'td', DEPENDS[i]);
        }
    }

    // Soft Depends
    var row;
    if (SOFTDEPENDS.length > 0) {
        addTag(content, 'header', 'Soft Dependencies');
        var table = addTag(content, 'table');
        for (var i = 0; i < SOFTDEPENDS.length; i++) {
            row = addTag(table, 'tr');
            addTag(row, 'td', SOFTDEPENDS[i]);
        }
    }

    // Load Before
    var row;
    if (LOAD_BEFORE.length > 0) {
        addTag(content, 'header', 'Loads Before');
        var table = addTag(content, 'table');
        for (var i = 0; i < LOAD_BEFORE.length; i++) {
            row = addTag(table, 'tr');
            addTag(row, 'td', LOAD_BEFORE[i]);
        }
    }

    // Package table
    addTag(content, 'header', 'Packages');
    var table = addTag(content, 'table');
    for (var i = 0; i < PACKAGES.length; i++) {
        row = addTag(table, 'tr');
        var td = addTag(row, 'td', PACKAGES[i]);
        td.className = 'packageLink';
        td.packageIndex = i;
        td.onclick = linkClicked;
        //addTag(row, 'td', 'Not set so this is placeholder');
    }
}

// Sets up the content for a package
function setupPackage() {
    var content = getMainSection();
    addTag(content, 'h2', 'Package ' + PACKAGES[packageIndex]);

    addClassTable(content, 'Interfaces', INTERFACES[packageIndex]);
    addClassTable(content, 'Classes', CLASSES[packageIndex]);
    addClassTable(content, 'Enums', ENUMS[packageIndex]);
    addClassTable(content, 'Exceptions', EXCEPTIONS[packageIndex]);
    addClassTable(content, 'Annotations', ANNOTATIONS[packageIndex]);
}

// Sets up the content for a class
function setupClass() {
    var content = getMainSection();
    var c = getClass(classIndex);

    // Main information
    var header = c.type.charAt(0).toUpperCase() + c.type.slice(1) + ' ' + c.name;
    addTag(content, 'h2', header);
    addTag(content, 'code', c.scope + ' ' + (c.isStatic ? 'static ' : '') + (c.isFinal ? 'final ' : '') + (c.isAbstract ? 'abstract ' : '') + c.type + ' ' + c.name);
    if (c.ext) {
        var tag = addTag(content, 'code', 'extends ');
        tag.appendChild(linkClass(c.ext));
    }
    if (c.impl.length > 0) {
        var tag = addTag(content, 'code', 'implements ');
        for (var k = 0; k < c.impl.length; k++) {
            tag.appendChild(linkClass(c.impl[k]));
            if (k != c.impl.length - 1) tag.appendChild(linkClass(', '));
        }
    }
    addTag(content, 'hr');
    addTag(content, 'p', c.description);

    var table, row, td, text, i, j;

    // Fields
    if (c.fields.length > 1) {
        if (c.type == 'enum') {
            addTag(content, 'header', 'Enum Constants');
            table = addTag(content, 'table');
            for (i = 1; i < c.fields.length; i++) {
                row = addTag(table, 'tr');
                td = addTag(row, 'td');
                td.appendChild(linkElement(c.fields[i].name));
            }
        }
        else {
            addTag(content, 'header', 'Fields');
            table = addTag(content, 'table');
            for (i = 1; i < c.fields.length; i++) {
                row = addTag(table, 'tr');
                td = addTag(row, 'td');
                td.appendChild(linkClass(c.fields[i].type));
                td = addTag(row, 'td');
                td.appendChild(linkElement(c.fields[i].name));
            }
        }
    }

    // Constructors
    if (c.constructors.length > 0) {
        addTag(content, 'header', 'Constructors');
        table = addTag(content, 'table');
        for (i = 0; i < c.constructors.length; i++) {
            row = addTag(table, 'tr');
            td = addTag(row, 'td');
            td.appendChild(linkElement(c.constructors[i].name, c.constructors[i].name + i));
            addTag(td, 'span', '(');
            for (j = 0; j < c.constructors[i].params.length; j++) {
                td.appendChild(linkClass(c.constructors[i].params[j].type));
                addTag(td, 'span', ' ' + c.constructors[i].params[j].name + (j != c.constructors[i].params.length - 1 ? ', ' : ''));
            }
            addTag(td, 'span', ')');
        }
    }

    // Make sure there's at least one method and static method
    var oneMethod, oneStaticMethod;
    for (i = 0; i < c.methods.length; i++) {
        if (c.methods[i].isStatic) oneStaticMethod = true;
        else oneMethod = true;
    }

    // Methods
    if (oneMethod) {

        // Annotations
        if (c.type == 'annotation') {
            addTag(content, 'header', 'Optional Elements');
            table = addTag(content, 'table');
            for (i = 0; i < c.methods.length; i++) {
                if (c.methods[i].isStatic) continue;
                row = addTag(table, 'tr');
                td = addTag(row, 'td');
                td.appendChild(linkClass(c.methods[i].returnValue.type));
                td = addTag(row, 'td');
                td.appendChild(linkElement(c.methods[i].name, c.methods[i].name + i));
            }
        }

        // Normal methods
        else {
            addTag(content, 'header', 'Methods');
            table = addTag(content, 'table');
            for (i = 0; i < c.methods.length; i++) {
                if (c.methods[i].isStatic) continue;
                row = addTag(table, 'tr');
                td = addTag(row, 'td');
                td.appendChild(linkClass(c.methods[i].returnValue.type));
                td = addTag(row, 'td');
                td.appendChild(linkElement(c.methods[i].name, c.methods[i].name + i));
                addTag(td, 'span', '(');
                for (j = 0; j < c.methods[i].params.length; j++) {
                    td.appendChild(linkClass(c.methods[i].params[j].type));
                    addTag(td, 'span', ' ' + c.methods[i].params[j].name + (j != c.methods[i].params.length - 1 ? ', ' : ''));
                }
                addTag(td, 'span', ')');
            }
        }
    }

    // Static Methods
    if (oneStaticMethod) {
        addTag(content, 'header', 'Static Methods');
        table = addTag(content, 'table');
        for (i = 0; i < c.methods.length; i++) {
            if (!c.methods[i].isStatic) continue;
            row = addTag(table, 'tr');
            var td = addTag(row, 'td');
            td.appendChild(linkClass(c.methods[i].returnValue.type));
            td = addTag(row, 'td');
            td.appendChild(linkElement(c.methods[i].name, c.methods[i].name + i));
            addTag(td, 'span', '(');
            for (j = 0; j < c.methods[i].params.length; j++) {
                td.appendChild(linkClass(c.methods[i].params[j].type));
                addTag(td, 'span', ' ' + c.methods[i].params[j].name + (j != c.methods[i].params.length - 1 ? ', ' : ''));
            }
            addTag(td, 'span', ')');
        }
    }

    ///////////////////////////////////////////////
    //                                           //
    //  Full Element Descriptions and Summaries  //
    //                                           //
    ///////////////////////////////////////////////

    // Fields
    if (c.fields.length > 1) {
        if (c.type == 'enum') {
            addTag(content, 'header', 'Enum Constants Detail');
            for (i = 1; i < c.fields.length; i++) {
                var header = addTag(content, 'h3', c.fields[i].name);
                header.id = c.fields[i].name;
                addTag(header, 'code', 'public static final ' + c.name + ' ' + c.fields[i].name);
                addTag(header, 'p', c.fields[i].description);
                if (i != c.fields.length - 1) addTag(content, 'hr');
            }
        }
        else {
            addTag(content, 'header', 'Fields Detail');
            for (i = 1; i < c.fields.length; i++) {
                var header = addTag(content, 'h3', c.fields[i].name);
                header.id = c.fields[i].name;
                addTag(header, 'code', c.fields[i].scope + ' ' 
                    + (c.fields[i].isStatic ? 'static ' : '') 
                    + (c.fields[i].isFinal ? 'final ' : '')
                    + (c.fields[i].isAbstract ? 'abstract ' : '')
                    + c.fields[i].type + ' ' + c.fields[i].name);
                addTag(header, 'p', c.fields[i].description);
                if (i != c.fields.length - 1) addTag(content, 'hr');
            }
        }
    }

    // Constructors
    if (c.constructors.length > 0) {
        addTag(content, 'header', 'Constructors Detail');
        for (i = 0; i < c.constructors.length; i++) {
            var header = addTag(content, 'h3', c.name);
            header.id = c.name + i;
            text = c.constructors[i].scope + ' ' + c.name + '(';
            for (j = 0; j < c.constructors[i].params.length; j++) {
                text += c.constructors[i].params[j].type + ' ' + c.constructors[i].params[j].name;
                if (j != c.constructors[i].params.length - 1) text += ', ';
            }
            addTag(header, 'code', text + ')');
            addTag(header, 'p', c.constructors[i].description);
            if (c.constructors[i].params.length > 0) {
                var subHeader = addTag(header, 'h4', 'params:');
                for (j = 0; j < c.constructors[i].params.length; j++) {
                    addTag(subHeader, 'p', '<em>' + c.constructors[i].params[j].name + ' - </em>' + c.constructors[i].params[j].description);
                }
            }
            if (i != c.constructors.length - 1) addTag(content, 'hr');
        }
    }

    // Methods
    if (oneMethod) {

        // Annotations
        if (c.type == "annotation") {
            for (i = 0; i < c.methods.length; i++) {
                if (c.methods[i].isStatic) continue;
                var header = addTag(content, 'h3', c.methods[i].name);
                header.id = c.methods[i].name + i;
                addTag(header, 'code', c.methods[i].scope + ' abstract ' + c.methods[i].returnValue.type + ' ' + c.methods[i].name);
                addTag(header, 'p', c.methods[i].description);
                var subHeader = addTag(header, 'h4', 'Returns:');
                addTag(subHeader, 'p', c.methods[i].returnValue.description);
                addTag(content, 'hr');
            }
        }

        // Normal methods
        else {
            addTag(content, 'header', 'Methods Detail');
            for (i = 0; i < c.methods.length; i++) {
                if (c.methods[i].isStatic) continue;
                var header = addTag(content, 'h3', c.methods[i].name);
                header.id = c.methods[i].name + i;
                text = c.methods[i].scope + ' ' + c.methods[i].returnValue.type + ' ' + c.methods[i].name + '(';
                for (j = 0; j < c.methods[i].params.length; j++) {
                    text += c.methods[i].params[j].type + ' ' + c.methods[i].params[j].name;
                    if (j != c.methods[i].params.length - 1) text += ', ';
                }
                addTag(header, 'code', text + ')');
                addTag(header, 'p', c.methods[i].description);
                if (c.methods[i].params.length > 0) {
                    var subHeader = addTag(header, 'h4', 'Parameters:');
                    for (j = 0; j < c.methods[i].params.length; j++) {
                        addTag(subHeader, 'p', '<em>' + c.methods[i].params[j].name + ' - </em>' + c.methods[i].params[j].description);
                    }
                }
                if (c.methods[i].returnValue.type != 'void') {
                    var subHeader = addTag(header, 'h4', 'Returns:');
                    addTag(subHeader, 'p', c.methods[i].returnValue.description);
                }
                addTag(content, 'hr');
            }
        }
        content.removeChild(content.lastChild);
    }

    // Static Methods
    if (oneStaticMethod) {
        addTag(content, 'header', 'Static Methods Detail');
        for (i = 0; i < c.methods.length; i++) {
            if (!c.methods[i].isStatic) continue;
            var header = addTag(content, 'h3', c.methods[i].name);
            header.id = c.methods[i].name + i;
            text = c.methods[i].scope + ' ' + c.methods[i].returnValue.type + ' ' + c.methods[i].name + '(';
            for (j = 0; j < c.methods[i].params.length; j++) {
                text += c.methods[i].params[j].type + ' ' + c.methods[i].params[j].name;
                if (j != c.methods[i].params.length - 1) text += ', ';
            }
            addTag(header, 'code', text + ')');
            addTag(header, 'p', c.methods[i].description);
            if (c.methods[i].params.length > 0) {
                var subHeader = addTag(header, 'h4', 'params:');
                for (j = 0; j < c.methods[i].params.length; j++) {
                    addTag(subHeader, 'p', '<em>' + c.methods[i].params[j].name + ' - </em>' + c.methods[i].params[j].description);
                }
            }
            if (c.methods[i].returnValue.type != 'void') {
                var subHeader = addTag(header, 'h4', 'Returns:');
                addTag(subHeader, 'p', c.methods[i].returnValue.description);
            }
            addTag(content, 'hr');
        }
        content.removeChild(content.lastChild);
    }
}

// Retrieves the main HTML section
function getMainSection() {
    return document.querySelector('#main');
}

// Updates the list of classes in the navigation
var classId;
function updateClasses() {
    var cList = document.querySelector('#classList');
    while (cList.hasChildNodes()) {
        cList.removeChild(cList.lastChild);
    }
    classId = 0;
    if (packageIndex == PACKAGES.length) {
        addTag(cList, 'p', 'Classes');
        for (var i = 0; i < ALL_CLASSES.length; i++, classId++) {
            var parts = ALL_CLASSES[i].split(':');
            var link = addTag(cList, 'a', parts[parts.length - 1]);
            link.className = 'classLink';
            link.package = parts[0];
            link.onclick = linkClicked;
            cList.appendChild(link);
        }
    }
    else {
        addClassList(cList, 'Interfaces', INTERFACES[packageIndex]);
        addClassList(cList, 'Classes', CLASSES[packageIndex]);
        addClassList(cList, 'Enums', ENUMS[packageIndex]);
        addClassList(cList, 'Exceptions', EXCEPTIONS[packageIndex]);
        addClassList(cList, 'Annotations', ANNOTATIONS[packageIndex]);
    }
}

function addClassList(element, title, list) {
    if (list.length == 0) return;
    addTag(element, 'p', title);
    for (var i = 0; i < list.length; i++, classId++) {
        var link = document.createElement('a');
        link.innerHTML = list[i].name;
        link.className = 'classLink';
        if (classId == classIndex) {
            link.className += ' selectedClass';
        }
        link.onclick = linkClicked;
        element.appendChild(link);
    }
}

function addClassTable(element, title, list) {
    if (list.length == 0) return;
    addTag(element, 'header', title);
    var table = addTag(element, 'table');
    var row;
    for (var i = 0; i < list.length; i++, classId++) {
        row = addTag(table, 'tr');
        var link = addTag(row, 'td', list[i].name);
        link.className = 'classLink';
        link.onclick = linkClicked;
        var desc = list[i].description;
        var copyright = desc.indexOf('©');
        var space = desc.indexOf(' ', copyright + 10);
        if (copyright > 0 && space > 0) desc = desc.substr(space + 1);
        else if (copyright > 0) desc = 'No description provided';
        while (desc.indexOf('<br>') == 0) desc = desc.substr(4);
        var desc = addTag(row, 'td', desc.split('</p>')[0].replace('<p>', '').split('. ')[0] + '.');
        link.style.maxHeight = link.style.height = (desc.clientHeight - 14) + 'px';
    }
}

function getClassId(name) {
    classId = 0;
    var id = checkClassId(name, INTERFACES[packageIndex]);
    if (id == -1) id = checkClassId(name, CLASSES[packageIndex]);
    if (id == -1) id = checkClassId(name, ENUMS[packageIndex]);
    if (id == -1) id = checkClassId(name, EXCEPTIONS[packageIndex]);
    if (id == -1) id = checkClassId(name, ANNOTATIONS[packageIndex]);
    return id;
}

function getClass(id) {
    classId = 0;
    var valid = checkClass(id, INTERFACES[packageIndex]);
    if (!valid) valid = checkClass(id, CLASSES[packageIndex]);
    if (!valid) valid = checkClass(id, ENUMS[packageIndex]);
    if (!valid) valid = checkClass(id, EXCEPTIONS[packageIndex]);
    if (!valid) valid = checkClass(id, ANNOTATIONS[packageIndex]);
    return valid;
}

function checkClassId(name, list) {
    for (var i = 0; i < list.length; i++, classId++) {
        if (list[i].name == name) return classId;
    }
    return -1;
}

function checkClass(id, list) {
    if (classId + list.length <= id) {
        classId += list.length;
        return false;
    }
    else return list[id - classId];
}

// Adds a title to the element using the 'p' tag
function addTag(element, tag, text) {
    var p = document.createElement(tag);
    if (text) p.innerHTML = text;
    element.appendChild(p);
    return p;
}
