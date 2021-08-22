/**
 * Represents the data for a dynamic class
 *
 * @param {string} name - name of the class
 *
 * @constructor
 */ 
function Class(name) 
{
	this.dataKey = 'attributes';
	this.componentKey = 'classes do not have components';
    this.attribCount = 0;
	
	// Class data
	this.data = [
		new StringValue('Name', 'name', name).setTooltip('The name of the class. This should not contain color codes'),
		new StringValue('Prefix', 'prefix', '&6' + name).setTooltip('The prefix given to players who profess as the class which can contain color codes'),
		new StringValue('Group', 'group', 'class').setTooltip('A class group are things such as "race", "class", and "trade". Different groups can be professed through at the same time, one class from each group'),
		new StringValue('Mana Name', 'mana', '&2Mana').setTooltip('The name the class uses for mana'),
		new IntValue('Max Level', 'max-level', 40).setTooltip('The maximum level the class can reach. If this class turns into other classes, this will also be the level it can profess into those classes.'),
		new ListValue('Parent', 'parent', ['None'], 'None').setTooltip('The class that turns into this one. For example, if Fighter turns into Knight, then Knight would have its parent as Fighter'),
		new ListValue('Permission', 'needs-permission', ['True', 'False'], 'False').setTooltip('Whether or not the class requires a permission to be professed as. The permission would be "skillapi.class.{className}"'),
        new ByteListValue('Exp Sources', 'exp-source', [ 'Mob', 'Block Break', 'Block Place', 'Craft', 'Command', 'Special', 'Exp Bottle', 'Smelt', 'Quest' ], 273).setTooltip('The experience sources the class goes up from. Most of these only work if "use-exp-orbs" is enabled in the config.yml.'),
		new AttributeValue('Health', 'health', 20, 0).setTooltip('The amount of health the class has'),
		new AttributeValue('Mana', 'mana', 20, 0).setTooltip('The amount of mana the class has'),
		new DoubleValue('Mana Regen', 'mana-regen', 1, 0).setTooltip('The amount of mana the class regens each interval. The interval is in the config.yml and by default is once every second. If you want to regen a decimal amount per second, increase the interval.'),
		new ListValue('Skill Tree', 'tree', [ 'Basic Horizontal', 'Basic Vertical', 'Level Horizontal', 'Level Vertical', 'Flood', 'Requirement' ], 'Requirement'),
		new StringListValue('Skills (one per line)', 'skills', []).setTooltip('The skills the class is able to use'),
		new ListValue('Icon', 'icon', materialList, 'Jack O Lantern').setTooltip('The item that represents the class in GUIs'),
		new IntValue('Icon Data', 'icon-data', 0).setTooltip('The data/durability value of the item that represents the class in GUIs'),
		new StringListValue('Icon Lore', 'icon-lore', [
			'&d' + name
		]),
		new StringListValue('Unusable Items', 'blacklist', [ ]).setTooltip('The types of items that the class cannot use (one per line)'),
		new StringValue('Action Bar', 'action-bar', '').setTooltip('The format for the action bar. Leave blank to use the default formatting.')
	];
    
    this.updateAttribs(10);
}

Class.prototype.updateAttribs = function(i)
{
    var j = 0;
    var back = {};
    while (this.data[i + j] instanceof AttributeValue)
    {
        back[this.data[i + j].key.toLowerCase()] = this.data[i + j];
        j++;
    }
    this.data.splice(i, this.attribCount);
    this.attribCount = 0;
    for (j = 0; j < ATTRIBS.length; j++)
    {
        var attrib = ATTRIBS[j].toLowerCase();
        var format = attrib.charAt(0).toUpperCase() + attrib.substr(1);
        this.data.splice(i + j, 0, new AttributeValue(format, attrib.toLowerCase(), 0, 0)
            .setTooltip('The amount of ' + attrib + ' the class should have')
        );
        if (back[attrib]) 
        {
            var old = back[attrib];
            this.data[i + j].base = old.base;
            this.data[i + j].scale = old.scale;
        }
        this.attribCount++;
    }
};

/**
 * Creates the form HTML for editing the class and applies it to
 * the appropriate area on the page
 */
Class.prototype.createFormHTML = function()
{
	var form = document.createElement('form');
	
	var header = document.createElement('h4');
	header.innerHTML = 'Class Details';
	form.appendChild(header);
	
	var h = document.createElement('hr');
	form.appendChild(h);
	
	this.data[5].list.splice(1, this.data[5].list.length - 1);
	for (var i = 0; i < classes.length; i++)
	{
		if (classes[i] != this) 
		{
			this.data[5].list.push(classes[i].data[0].value);
		}
	}
	for (var i = 0; i < this.data.length; i++)
	{
		this.data[i].createHTML(form);
        
        // Append attributes
        if (this.data[i].name == 'Mana')
        {
            var dragInstructions = document.createElement('label');
            dragInstructions.id = 'attribute-label';
            dragInstructions.innerHTML = 'Drag/Drop your attributes.yml file to see custom attributes';
            form.appendChild(dragInstructions);
            this.updateAttribs(i + 1);
        }
	}
	
	var hr = document.createElement('hr');
	form.appendChild(hr);
	
	var save = document.createElement('h5');
	save.innerHTML = 'Save Class',
	save.classData = this;
	save.addEventListener('click', function(e) {
		this.classData.update();
		saveToFile(this.classData.data[0].value + '.yml', this.classData.getSaveString());
	});
	form.appendChild(save);
	
	var del = document.createElement('h5');
	del.innerHTML = 'Delete',
	del.className = 'cancelButton';
	del.addEventListener('click', function(e) {
		var list = document.getElementById('classList');
		var index = list.selectedIndex;
		
		classes.splice(index, 1);
		if (classes.length == 0)
		{
			newClass();
		}
		list.remove(index);
		index = Math.min(index, classes.length - 1);
		activeClass = classes[index];
		list.selectedIndex = index;
	});
	form.appendChild(del);
	
	var target = document.getElementById('classForm');
	target.innerHTML = '';
	target.appendChild(form);
};

/**
 * Updates the class data from the details form if it exists
 */
Class.prototype.update = function()
{
	var index;
	var list = document.getElementById('classList');
	for (var i = 0; i < classes.length; i++)
	{
		if (classes[i] == this)
		{
			index = i;
			break;
		}
	}
	var prevName = this.data[0].value;
	for (var j = 0; j < this.data.length; j++)
	{
		this.data[j].update();
	}
	var newName = this.data[0].value;
	this.data[0].value = prevName;
	if (isClassNameTaken(newName)) return;
	this.data[0].value = newName;
	list[index].text = this.data[0].value;
};

/**
 * Creates and returns a save string for the class
 */ 
Class.prototype.getSaveString = function()
{
	var saveString = '';
	
	saveString += this.data[0].value + ":\n";
	for (var i = 0; i < this.data.length; i++)
	{
		if (this.data[i] instanceof AttributeValue) continue;
		saveString += this.data[i].getSaveString('  ');
	}
	saveString += '  attributes:\n';
	for (var i = 0; i < this.data.length; i++)
	{
		if (this.data[i] instanceof AttributeValue)
		{
			saveString += this.data[i].getSaveString('    ');
		}
	}
	return saveString;
};

/**
 * Loads class data from the config lines stating at the given index
 *
 * @param {YAMLObject} data - the data to load
 *
 * @returns {Number} the index of the last line of data for this class
 */
Class.prototype.load = loadSection;

/**
 * Creates a new class and switches the view to it
 *
 * @returns {Class} the new class
 */ 
function newClass()
{
	var id = 1;
	while (isClassNameTaken('Class ' + id)) id++;
	
	activeClass = addClass('Class ' + id);
	
	var list = document.getElementById('classList');
	list.selectedIndex = list.length - 2;
	
	activeClass.createFormHTML();
	
	return activeClass;
}

/**
 * Adds a skill to the editor without switching the view to it
 *
 * @param {string} name - the name of the skill to add
 *
 * @returns {Skill} the added skill
 */ 
function addClass(name) 
{
	var c = new Class(name);
	classes.push(c);
	
	var option = document.createElement('option');
	option.text = name;
	var list = document.getElementById('classList');
	list.add(option, list.length - 1);
	
	return c;
}

/**
 * Checks whether or not a class name is currently taken
 *
 * @param {string} name - name to check for
 *
 * @returns {boolean} true if the name is taken, false otherwise
 */ 
function isClassNameTaken(name)
{
	return getClass(name) != null;
}

/**
 * Retrieves a class by name
 *
 * @param {string} name - name of the class to retrieve
 *
 * @returns {Class} the class with the given name or null if not found
 */
function getClass(name)
{
	name = name.toLowerCase();
	for (var i = 0; i < classes.length; i++)
	{
		if (classes[i].data[0].value.toLowerCase() == name) return classes[i];
	}
	return null;
}

var activeClass = new Class('Class 1');
var classes = [activeClass];
activeClass.createFormHTML();