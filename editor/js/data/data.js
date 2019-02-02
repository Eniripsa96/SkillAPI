let DATA = {};

const version = (localStorage.getItem('server-version') || '1.13').substr(2);

depend('data/1.8', function() { if (version === '8') DATA = DATA_8 });7
depend('data/1.9', function() { if (version === '9') DATA = DATA_9 });
depend('data/1.10', function() { if (version === '10') DATA = DATA_10 });
depend('data/1.11', function() { if (version === '11') DATA = DATA_11 });
depend('data/1.12', function() { if (version === '12') DATA = DATA_12 });
depend('data/1.13', function() { if (version === '13') DATA = DATA_13 });

function getMaterials() {
    return DATA.MATERIALS;
}

function getAnyMaterials() {
    return [ 'Any', ...DATA.MATERIALS ];
}

function getSounds() {
    return DATA.SOUNDS;
}

function getEntities() {
    return DATA.ENTITIES;
}

function getParticles() {
    return DATA.PARTICLES || [];
}

function getBiomes() {
    return DATA.BIOMES;
}

function getDamageTypes() {
    return DATA.DAMAGE_TYPES;
}

function getPotionTypes() {
    return DATA.POTIONS;
}

function getAnyPotion() {
    return [ 'Any', ...DATA.POTIONS ];
}

function getGoodPotions() {
    const list = DATA.POTIONS.filter(type => GOOD_POTIONS.includes(type));
    return [ 'All', 'None', ...list ];
}

function getBadPotions() {
    const list = DATA.POTIONS.filter(type => BAD_POTIONS.includes(type));
    return [ 'All', 'None', ...list ];
}

function getDyes() {
    return DYES;
}

const GOOD_POTIONS = [
    "Speed",
    "Fast Digging",
    "Increase Damage",
    "Jump",
    "Regeneration",
    "Damage Resistance",
    "Fire Resistance",
    "Water Breathing",
    "Invisibility",
    "Night Vision",
    "Health Boost",
    "Absorption",
    "Saturation",
    "Glowing",
    "Luck",
    "Slow Falling",
    "Conduit Power",
    "Dolphins Grace"
];

const BAD_POTIONS = [
    "Slow",
    "Slow Digging",
    "Confusion",
    "Blindness",
    "Hunger",
    "Weakness",
    "Poison",
    "Wither",
    "Levitation",
    "Unluck"
];

const DYES = [
    'BLACK',
    'BLUE',
    'BROWN',
    'CYAN',
    'GRAY',
    'GREEN',
    'LIGHT_BLUE',
    'LIGHT_GRAY',
    'LIME',
    'MAGENTA',
    'ORANGE',
    'PINK',
    'PURPLE',
    'RED',
    'WHITE',
    'YELLOW'
];