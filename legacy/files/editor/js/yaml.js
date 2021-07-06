/**
 * RegEx patterns used by the YAML parser
 */ 
var Regex = {
	INT: /^-?[0-9]+$/,
	FLOAT: /^-?[0-9]+\.[0-9]+$/
};

/**
 * Parses the YAML data string
 *
 * @param {string} text - the YAML data string
 *
 * @returns {YAMLObject} the parsed data
 */ 
function parseYAML(text)
{
	text = text.replace(/\r\n/g, '\n').replace(/\n *\n/g, '\n').replace(/ +\n/g, '\n');
	var data = new YAMLObject();
	var index = 0;
	var lines = text.split('\n');
	data.parse(lines, index, 0);
	return data;
}

/**
 * Counts the number of leading spaces in a string
 *
 * @param {string} string - the string to check
 *
 * @returns {Number} the number of leading spaces
 */
function countSpaces(string) {
	for (var result = 0, characterCode = string.charCodeAt(0); 32 == characterCode;) {
		characterCode = string.charCodeAt(++result);
	}
	return result;
}

/**
 * Represents a collection of YAML data (ConfigurationSection in Bukkit)
 */
function YAMLObject(key) { }

/**
 * Checks whether or not the YAML data contains a value under the given key
 *
 * @param {string} key - key of the value to check for
 *
 * @returns {boolean} true if contains the value, false otherwise
 */
YAMLObject.prototype.has = function(key)
{
	return this[key] !== undefined;
}

/**
 * Retrieves a value from the data
 *
 * @param {string} key   - key of the value to retrieve
 * @param {Object} value - default value to return if one isn't found
 *
 * @returns {string} the obtained value
 */ 
YAMLObject.prototype.get = function(key, value) 
{
	return this.has(key) ? this[key] : value;
}

/**
 * Parses YAML data using the provided parameters
 *
 * @param {Array}  lines  - the lines of the YAML data
 * @param {Number} index  - the starting index of the data to parse
 * @param {Number} indent - the number of spaces preceeding the keys of the data
 *
 * @returns {Number} the ending index of the parsed data
 */
YAMLObject.prototype.parse = function(lines, index, indent)
{
	while (index < lines.length && countSpaces(lines[index]) >= indent)
	{
		while (index < lines.length && (countSpaces(lines[index]) != indent || lines[index].replace(/ /g, '').charAt(0) == '#' || lines[index].indexOf(':') == -1)) index++;
		if (index == lines.length) return index;
		
		var key = lines[index].substring(indent, lines[index].indexOf(':'));
		
        // New empty section
		if (lines[index].indexOf(": {}") == lines[index].length - 4 && lines[index].length >= 4)
		{
			this[key] = {};
		}
        
		// String list
		else if (index < lines.length - 1 
            && lines[index + 1].charAt(indent) == '-'
            && lines[index + 1].charAt(indent + 1) == ' ' 
            && countSpaces(lines[index + 1]) == indent)
		{
			var stringList = [];
			while (++index < lines.length && lines[index].charAt(indent) == '-')
			{
				var str = lines[index].substring(indent + 2);
				if (str.charAt(0) == '\'') str = str.substring(1, str.length - 1);
				else if (str.charAt(0) == '"') str = str.substring(1, str.length - 1);
				stringList.push(str);
			}
			this[key] = stringList;
			index--;
		}
		
		// New section with content
		else if (index < lines.length - 1 && countSpaces(lines[index + 1]) > indent)
		{
			index++;
			var newIndent = countSpaces(lines[index]);
			var newData = new YAMLObject();
			index = newData.parse(lines, index, newIndent) - 1;
			this[key] = newData;
		}
		
		// Regular value
		else
		{
			var value = lines[index].substring(lines[index].indexOf(':') + 2);
			if (value.charAt(0) == '\'') value = value.substring(1, value.length - 1);
            else if (!isNaN(value)) {
                if (Regex.INT.test(value)) value = parseInt(value);
                else value = parseFloat(value);
            }
			this[key] = value;
		}
		
		do
		{
			index++;
		}
		while (index < lines.length && lines[index].replace(/ /g, '').charAt(0) == '#');
	}
	return index;
}
