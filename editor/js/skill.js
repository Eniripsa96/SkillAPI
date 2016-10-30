/**
 * Represents the data for a dynamic skill
 *
 * @param {string} name - the name of the skill
 *
 * @constructor
 */
function Skill(name)
{
	this.components = [];

	// Included to simplify code when adding components
	this.html = document.getElementById('builderContent');
	
	this.dataKey = 'attributes';
	this.componentKey = 'components';
	
	// Skill data
    var iconList = materialList.slice(0);
    iconList.splice(materialList.indexOf('Arrow'), 1);
	this.data = [
		new StringValue('Name', 'name', name).setTooltip('The name of the skill. This should not contain color codes'),
		new StringValue('Type', 'type', 'Dynamic').setTooltip('The flavor text describing the skill such as "AOE utility" or whatever you want it to be'),
		new IntValue('Max Level', 'max-level', 5).setTooltip('The maximum level the skill can reach'),
		new ListValue('Skill Req', 'skill-req', ['None'], 'None').setTooltip('The skill that needs to be upgraded before this one can be unlocked'),
		new IntValue('Skill Req Level', 'skill-req-lvl', 1).setTooltip('The level that the required skill needs to reach before this one can be unlocked'),
		new ListValue('Permission', 'needs-permission', ['True', 'False'], 'False').setTooltip('Whether or not this skill requires a permission to unlock. The permission would be "skillapi.skill.{skillName}"'),
		new AttributeValue('Level Req', 'level', 1, 0).setTooltip('The class level the player needs to be before unlocking or upgrading this skill'),
		new AttributeValue('Cost', 'cost', 1, 0).setTooltip('The amount of skill points needed to unlock and upgrade this skill'),
		new AttributeValue('Cooldown', 'cooldown', 0, 0).setTooltip('The time in seconds before the skill can be cast again (only works with the Cast trigger)'),
		new AttributeValue('Mana', 'mana', 0, 0).setTooltip('The amount of mana it takes to cast the skill (only works with the Cast trigger)'),
		new StringValue('Cast Message', 'msg', '&6{player} &2has cast &6{skill}').setTooltip('The message to display to players around the caster when the skill is cast. The radius of the area is in the config.yml options'),
        new StringValue('Combo', 'combo', '').setTooltip('The click combo to assign the skill (if enabled). Use L, R, and S for the types of clicks separated by spaces. For example, "L L R R" would work for 4 click combos.'),
		new ListValue('Icon', 'icon', iconList, 'Jack O Lantern').setTooltip('The item used to represent the skill in skill trees'),
		new IntValue('Icon Data', 'icon-data', 0).setTooltip('The data/durability value of the item used to represent the skill in skill trees'),
		new StringListValue('Icon Lore', 'icon-lore', [
			'&d{name} &7({level}/{max})',
			'&2Type: &6{type}',
			'',
			'{req:level}Level: {attr:level}',
			'{req:cost}Cost: {attr:cost}',
			'',
			'&2Mana: {attr:mana}',
			'&2Cooldown: {attr:cooldown}'
		]).setTooltip('The description shown for the item in skill trees. Include values of mechanics such as damage dealt using their "Icon Key" values')
	];
}

/**
 * Applies the skill data to the HTML page, overwriting any previous data
 */ 
Skill.prototype.apply = function() 
{
	var builder = document.getElementById('builderContent');
	builder.innerHTML = '';
	
	// Set up the builder content
	for (var i = 0; i < this.components.length; i++)
	{
		this.components[i].createBuilderHTML(builder);
	}
}

/**
 * Creates the form HTML for editing the skill and applies it to
 * the appropriate area on the page
 */
Skill.prototype.createFormHTML = function()
{
	var form = document.createElement('form');
	
	var header = document.createElement('h4');
	header.innerHTML = 'Skill Details';
	form.appendChild(header);
	
	var h = document.createElement('hr');
	form.appendChild(h);
	
	this.data[3].list.splice(1, this.data[3].list.length - 1);
	for (var i = 0; i < skills.length; i++)
	{
		if (skills[i] != this) 
		{
			this.data[3].list.push(skills[i].data[0].value);
		}
	}
	for (var i = 0; i < this.data.length; i++)
	{
		this.data[i].createHTML(form);
	}
	
	var hr = document.createElement('hr');
	form.appendChild(hr);
	
	var done = document.createElement('h5');
	done.className = 'doneButton';
	done.innerHTML = 'Edit Effects',
	done.skill = this;
	done.form = form;
	done.addEventListener('click', function(e) {
		this.skill.update();
		var list = document.getElementById('skillList');
		list[list.selectedIndex].text = this.skill.data[0].value;
		this.form.parentNode.removeChild(this.form);
		showSkillPage('builder');
	});
	form.appendChild(done);
	
	var target = document.getElementById('skillForm');
	target.innerHTML = '';
	target.appendChild(form);
}

/**
 * Updates the skill data from the details form if it exists
 */
Skill.prototype.update = function()
{
	var index;
	var list = document.getElementById('skillList');
	for (var i = 0; i < skills.length; i++)
	{
		if (skills[i] == this)
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
	if (isSkillNameTaken(newName)) return;
	this.data[0].value = newName;
	list[index].text = this.data[0].value;
}

/**
 * Checks whether or not the skill is using a given trigger
 *
 * @param {string} trigger - name of the trigger to check
 *
 * @returns {boolean} true if using it, false otherwise
 */ 
Skill.prototype.usingTrigger = function(trigger)
{
	for (var i = 0; i < this.components.length; i++)
	{
		if (this.components[i].name == trigger) return true;
	}
	return false;
}

/**
 * Creates and returns a save string for the skill
 */ 
Skill.prototype.getSaveString = function()
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
	if (this.components.length > 0)
	{
		saveString += '  components:\n';
		saveIndex = 0;
		for (var i = 0; i < this.components.length; i++)
		{
			saveString += this.components[i].getSaveString('    ');
		}
	}
	return saveString;
}

/**
 * Loads skill data from the config lines stating at the given index
 *
 * @param {YAMLObject} data - the data to load
 *
 * @returns {Number} the index of the last line of data for this skill
 */
Skill.prototype.load = function(data)
{
	if (data.active || data.embed || data.passive)
	{
		// Load old skill config for conversion
	}
	else 
	{
		this.loadBase(data);
	}
}

Skill.prototype.loadBase = loadSection;

/**
 * Creates a new skill and switches the view to it
 *
 * @returns {Skill} the new skill
 */ 
function newSkill()
{
	var id = 1;
	while (isSkillNameTaken('Skill ' + id)) id++;
	
	activeSkill = addSkill('Skill ' + id);
	
	var list = document.getElementById('skillList');
	list.selectedIndex = list.length - 2;
	
	activeSkill.apply();
	activeSkill.createFormHTML();
	showSkillPage('skillForm');
	
	return activeSkill;
}

/**
 * Adds a skill to the editor without switching the view to it
 *
 * @param {string} name - the name of the skill to add
 *
 * @returns {Skill} the added skill
 */ 
function addSkill(name) 
{
	var skill = new Skill(name);
	skills.push(skill);
	
	var option = document.createElement('option');
	option.text = name;
	var list = document.getElementById('skillList');
	list.add(option, list.length - 1);
	
	return skill;
}

/**
 * Checks whether or not a skill name is currently taken
 *
 * @param {string} name - name to check for
 *
 * @returns {boolean} true if the name is taken, false otherwise
 */ 
function isSkillNameTaken(name)
{
	return getSkill(name) != null;
}

/**
 * Retrieves a skill by name
 *
 * @param {string} name - name of the skill to retrieve
 *
 * @returns {Skill} the skill with the given name or null if not found
 */
function getSkill(name)
{
	name = name.toLowerCase();
	for (var i = 0; i < skills.length; i++)
	{
		if (skills[i].data[0].value.toLowerCase() == name) return skills[i];
	}
	return null;
}


var activeSkill = new Skill('Skill 1');
var activeComponent = undefined;
var skills = [activeSkill];
activeSkill.createFormHTML();
showSkillPage('skillForm');
