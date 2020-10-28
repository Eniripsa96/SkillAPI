# Used to automatically generate data for new Minecraft versions
# Required .java files in workspace:
# - org.bukkit.block.Biome
# - org.bukkit.event.entity.EntityDamageEvent
# - org.bukkit.Material
# - org.bukkit.Sound
# - org.bukkit.entity.EntityType
# - org.bukkit.potion.PotionEffectType
# - org.bukkit.Particle
import os

folder = os.getcwd()
requiredFiles = {'Biome.java', 'EntityDamageEvent.java', 'EntityType.java', 'Material.java', 'Particle.java', 'PotionEffectType.java', 'Sound.java'}
for (dirPath, dirNames, fileNames) in os.walk(folder):
    files = fileNames
    break;
assert requiredFiles.issubset(files),'Missing .java files in workspace'
version = input('Enter Minecraft version with the correct format (Example: 1.16): ')
version = version[2:]

def readEnum(fileName,enumStart):
    # Init sets
    lastChars = {',',';'}
    enumEnders = {'private','public','@NotNull','@Override'}

    values = []
    readFile = open(fileName,'r')
    reachedEnum = False
    isDeprecated = False
    for line in readFile.readlines():
        line = line.strip()
        if (not reachedEnum) and (line == enumStart):
            # Reached enum
            reachedEnum = True
        elif reachedEnum:
            # Currently in enum
            if '(' in line:
                line = line[:line.find('(')]
            if line.isupper():
                # It´s a value
                if isDeprecated or line[:7] == 'LEGACY_':
                    # Is deprecated or legacy, don't add it
                    isDeprecated = False
                    continue;
                else:
                    #Add it
                    value = line[0].upper()+line[1:].lower().replace('_',' ')
                    if line[-1] in lastChars:
                        # Must remove last char
                        value = value[:-1]
                    values.append(value)
            elif line.split(' ',1)[0] in enumEnders:
                # Reached last value, stop
                break;
            elif line == '@Deprecated':
                isDeprecated = True
    readFile.close()
    return values

def readClass(fileName,instanceDefiner):
    values = []
    readFile = open(fileName,'r')
    isDeprecated = False
    for line in readFile.readlines():
        line = line.strip()
        if instanceDefiner in line:
            line = line[37:]
            line = line[:line.find(' ')]
            if line.isupper():
                # It´s a value
                if isDeprecated or line[:7] == 'LEGACY_':
                    # Is deprecated or legacy, don't add it
                    isDeprecated = False
                    continue;
                else:
                    #Add it
                    values.append(line[0].upper()+line[1:].lower().replace('_',' '))
        elif line == '@Deprecated':
            isDeprecated = True
    readFile.close()
    return values

def listToString(list):
    s = '['
    for entry in list:
        s = s + '\n        "' + entry + '",'
    return s[:-1] + '\n    ]'

#Get Biomes
biomes = readEnum('Biome.java','public enum Biome implements Keyed {')
assert len(biomes)>0,'Couldn\'t read any Biome in Biome.java'
#print(biomes)
print("Successfully read",len(biomes),"Biomes.")

    
# Get DamageTypes
damages = readEnum('EntityDamageEvent.java','public enum DamageCause {')
assert len(damages)>0,'Couldn\'t read any DamageType in EntityDamageEvent.java'
#print(damages)
print("Successfully read",len(damages),"DamageTypes.")

# Get EntityTypes
entities = readEnum('EntityType.java','public enum EntityType implements Keyed {')
assert len(entities)>0,'Couldn\'t read any Entity in EntityType.java'
#print(entities)
print("Successfully read",len(entities),"EntityTypes.")

# Get Materials
materials = readEnum('Material.java','public enum Material implements Keyed {')
assert len(materials)>0,'Couldn\'t read any Material in Material.java'
#print(materials)
print("Successfully read",len(materials),"Materials.")

# Get Particles
particles = readEnum('Particle.java','public enum Particle {')
assert len(particles)>0,'Couldn\'t read any Particle in Particle.java'
#print(particles)
print("Successfully read",len(particles),"Particles.")

# Get Effects
effects = readClass('PotionEffectType.java','public static final PotionEffectType')
assert len(effects)>0,'Couldn\'t read any PotionEffectType in PotionEffectType.java'
#print(effects)
print("Successfully read",len(effects),"PotionEffectTypes.")

# Get Sounds
sounds = readEnum('Sound.java','public enum Sound {')
assert len(sounds)>0,'Couldn\'t read any Sound in Sound.java'
#print(sounds)
print("Successfully read",len(sounds),"Sounds.")

# Generate javascript file
file = open(os.path.join(os.pardir,'1.'+version+'.js'),"w")
fileContent = 'var DATA_'+version+' = {\n    MATERIALS: '+listToString(materials)+',\n    SOUNDS: '+listToString(sounds)+',\n    ENTITIES: '+listToString(entities)+',\n    BIOMES: '+listToString(biomes)+',\n    POTIONS: '+listToString(effects)+',\n    PARTICLES: '+listToString(particles)+',\n    DAMAGE_TYPES: '+listToString(damages)+'\n};\n\nvar keys = Object.keys(DATA_'+version+');\nfor (var i = 0; i < keys.length; i++) {\n    DATA_'+version+'[keys[i]].sort();\n}\nDATA_'+version+'.ANY_POTION = DATA_'+version+'.POTIONS.slice().splice(0, 0, \'Any\');'
file.write(fileContent)
file.close()
print('1.'+version+'.js succesfully generated.')
