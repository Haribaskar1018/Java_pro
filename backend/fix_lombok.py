import os
import re

files_to_fix = [
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\service\ExpenseService.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\service\AuthService.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\security\JwtAuthFilter.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\config\SecurityConfig.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\controller\QrController.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\controller\GroupController.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\controller\ExpenseController.java",
    r"e:\splitwise-pro\backend\src\main\java\com\splitwisepro\controller\AuthController.java"
]

for file_path in files_to_fix:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    if '@RequiredArgsConstructor' not in content:
        continue

    # Remove the annotation
    content = content.replace('@RequiredArgsConstructor\n', '')
    content = content.replace('@RequiredArgsConstructor\r\n', '')
    content = content.replace('import lombok.RequiredArgsConstructor;\n', '')
    content = content.replace('import lombok.RequiredArgsConstructor;\r\n', '')

    # Find the class name
    class_match = re.search(r'public class (\w+)', content)
    if not class_match:
        continue
    class_name = class_match.group(1)

    # Find all final fields
    final_fields = re.findall(r'private final ([\w<>]+) (\w+);', content)
    if not final_fields:
        # Just write back without RequiredArgsConstructor
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        continue

    # Construct the constructor
    constructor_args = ', '.join([f"{type_} {name}" for type_, name in final_fields])
    constructor_body = '\n'.join([f"        this.{name} = {name};" for type_, name in final_fields])
    
    constructor = f"\n    public {class_name}({constructor_args}) {{\n{constructor_body}\n    }}\n"

    # Insert constructor after the last final field
    last_field_match = list(re.finditer(r'private final [\w<>]+ \w+;', content))[-1]
    insert_pos = last_field_match.end()
    
    new_content = content[:insert_pos] + "\n" + constructor + content[insert_pos:]
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print(f"Fixed {file_path}")
