#!/usr/bin/env bash

shopt -s globstar

files_checked=0
declare -A warn_files # Array to hold all files that give warning

for file in src/**/*.java; do

    if [[ -f "$file" ]]; then
        echo "Checking file: $file"
        output=$(./mvnw exec:java@strange -Dexec.args="$file" 2>&1)

        # Allow script to be terminated with CTRL+C
        if [[ $? -ne 0 ]]; then
            echo -e "\n$files_checked files checked"
            exit 1
        fi
        ((files_checked++))

        # Extract warning lines
        warnings=$(echo "$output" | grep '\[WARN\]')
    

        # If warnings exist, store them in warn_files array
        if [[ -n "$warnings" ]]; then
            filename=$(basename "$file")
            warn_files["$filename"]="$warnings"
        fi
    fi
done

echo "All $files_checked files checked"

# Print files with warnings, if any
if [[ ${#warn_files[@]} -gt 0 ]]; then
    echo -e "\nFiles with warnings:\n"
    
    for filename in "${!warn_files[@]}"; do
        echo -e "\033[0;91m$filename\033[0m"
        echo "${warn_files[$filename]}"
        echo ""
    done
else
    echo -e "\nNo warnings detected."
fi