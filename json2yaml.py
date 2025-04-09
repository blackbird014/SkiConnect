import json
import yaml
import sys

# Read JSON file
with open('openapi.json', 'r') as json_file:
    json_data = json.load(json_file)

# Convert to YAML
yaml_data = yaml.dump(json_data, sort_keys=False, allow_unicode=True)

# Write YAML file
with open('openapi.yaml', 'w') as yaml_file:
    yaml_file.write(yaml_data)

print("Conversion completed. YAML file created: openapi.yaml") 