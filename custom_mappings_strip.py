#!/bin/python3

input_file = "custom_mappings.tiny"
output_file = "custom_mappings_stripped.tiny"

with open(input_file, "r") as f:
    lines = f.readlines()

with open(output_file, "w") as f:
    for line in lines:
        stripped = line.rstrip("\n")
        leading_tabs = len(stripped) - len(stripped.lstrip("\t"))
        parts = stripped.lstrip("\t").split("\t")

        if len(parts) < 2:
            f.write(line)
            continue

        # remove third-to-last column (i.e. 'official')
        new_parts = parts[:-3] + parts[-2:]
        f.write(("\t" * leading_tabs) + "\t".join(new_parts) + "\n")
