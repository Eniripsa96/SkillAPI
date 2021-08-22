// The table of loaded scripts
var SCRIPT_TAGS = {
    scriptCount: 0,
    onload     : function(e) {
        SCRIPT_TAGS[e.target.id].loaded = true;
        SCRIPT_TAGS.scriptCount--;
        if (SCRIPT_TAGS.scriptCount == 0) {
            updateLoader();
        }
    }
};

// The pending extensions
var EXTENSIONS = {};

/**
 * Makes the subclass extend the base class by copying
 * prototype methods over. Also provides a superconstructor
 * using "this.super(<params>)".
 *
 * @param {string} sub  - the sub class doing the extending
 * @param {string} base - the base class being extended
 */
function extend(sub, base) {
    if (!EXTENSIONS[sub]) EXTENSIONS[sub] = [];
    EXTENSIONS[sub].push(base);
}

/**
 * Updates the loader, launching an event when done
 */
function updateLoader() {
    if (SCRIPT_TAGS.scriptCount > 0) return;

    // Extensions
    for (var subName in EXTENSIONS) {
        applyExtensions(subName);
    }

    // Done event
    if (window['onLoaderDone']) {
        window['onLoaderDone']();
    }
}

/**
 * Applies the queued extensions for the class name
 *
 * @param key class name
 */
function applyExtensions(key) {
    var sub = window[key];
    var list = EXTENSIONS[key];
    for (var i = 0; i < list.length; i++) {
        var baseName = list[i];
        if (EXTENSIONS[baseName]) {
            applyExtensions(baseName);
        }
        var base = window[baseName];
        if (base && sub) {
            sub.prototype.super = superConstructor;
            sub.prototype.childConstructors = sub.prototype.childConstructors || [];
            for (var j = 0; base.prototype.childConstructors && j < base.prototype.childConstructors.length; j++) {
                sub.prototype.childConstructors.push(base.prototype.childConstructors[j]);
            }
            sub.prototype.childConstructors.push(base);
            for (x in base.prototype) {
                if (!sub.prototype[x]) {
                    sub.prototype[x] = base.prototype[x];
                }
            }
        }
    }
}

/**
 * Super constructor function for extended classes
 */
var superConstructorEnabled = true;
function superConstructor() {
    if (!superConstructorEnabled) return;
    superConstructorEnabled = false;
    for (var i = 0; i < this.childConstructors.length; i++) {
        this.childConstructors[i].apply(this, arguments);
    }
    superConstructorEnabled = true;
}

/**
 * Loads a script asynchronously, launching the callback
 * when the script is loaded or immediately if it is
 * already loaded.
 *
 * @param {string}   script   - name of the script to load
 * @param {function} [callback] - callback for when it's done loading
 */
function depend(script, callback) {

    var tag;

    // If already loaded, run the callback
    if (SCRIPT_TAGS[script]) {
        var data = SCRIPT_TAGS[script];
        if (data.loaded) {
            if (callback) callback();
        }
        else data.tag.addEventListener('load', callback);
    }
	
	else {
		var src = 'editor/js/' + script + '.js';
		var scripts = document.querySelectorAll('script');
		for (var i = 0; i < scripts.length; i++) 
		{
			if (scripts[i].src == src)
			{
				SCRIPT_TAGS[script] = { tag: scripts[i], loaded: true };
				if (callback) callback();
				return;
			}
		}
		
        var scriptTag = document.createElement('script');
        scriptTag.id = script;
        scriptTag.type = 'text/javascript';
        if (callback) scriptTag.addEventListener('load', callback);
        scriptTag.addEventListener('load', SCRIPT_TAGS.onload);
        SCRIPT_TAGS[script] = {tag: scriptTag, loaded: false};
        scriptTag.src = 'editor/js/' + script + '.js';
        document.querySelector('head').appendChild(scriptTag);
        SCRIPT_TAGS.scriptCount++;
    }
}