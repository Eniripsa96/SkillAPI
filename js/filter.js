/**
 * Filters an input box for integers by setting this
 * as the input event listener.
 */
function filterInt() {

    // Get the element
    var element = document.activeElement;
    
    // Remove non-numeric characters
    element.value = element.value.replace(/[^0-9-]/g, "");
    
    // Remove unnecessary 0's
    for (var i = 0; i < element.value.length - 1; i++) {
        var c = element.value.charAt(i);
        if (element.value.charAt(i) == '0') {
            element.value = element.value.replace('0', '');
            i--;
        }
        else if (c != '-') {
            break;
        }
    }
    
    // Remove extra negative signs
    if (element.value.lastIndexOf('-') > 0) {
        var negative = element.value.charAt(0) != '-';
        element.value = element.value.replace(/-/g, "");
        if (negative) {
            element.value = '-' + element.value;
        }
    }
    
    // Prevent it from being empty
    if (element.value.length == 0 || (element.value.length == 1 && element.value.charAt(0) == '-')) {
        element.value += '0';
    }
}

/**
 * Filters an input box for doubles by setting this
 * as the input event listener.
 */ 
function filterDouble() {

    // Get the data
    var element = document.activeElement;
    var negative = false;
    var index = -1;
    while ((index = element.value.indexOf("-", index + 1)) >= 0) {
        negative = !negative;
    }
    
    // Remove non-numeric characters besides periods
    var filtered = element.value.replace(/[^0-9\.-]/g, "");
    if (filtered != element.value)
	{
		element.value = filtered;
	}
	
    // Remove unnecessary 0's
    for (var i = 0; i < element.value.length - 1; i++) {
        var c = element.value.charAt(i);
        if (element.value.charAt(i) == '0' && element.value.charAt(i + 1) != '.') {
            element.value = element.value.replace('0', '');
            i--;
        }
        else if (c != '-') {
            break;
        }
    }
    
    // Remove extra negative signs
    if (element.value.lastIndexOf('-') > 0) {
        var negative = element.value.charAt(0) != '-';
        element.value = element.value.replace(/-/g, "");
        if (negative) {
            element.value = '-' + element.value;
        }
    }
    
    // Prevent it from being empty
    if (element.value.length == 0 || (element.value.length == 1 && element.value == "-")) {
		element.value += '0';
    }
}