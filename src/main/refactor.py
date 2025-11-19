#!/usr/bin/env python3
import os
import re

# Controller目录路径
controller_dir = "/Users/xuj/IdeaProjects/cyyai/src/main/java/com/xyes/springboot/controller"

# 需要处理的文件列表
files_to_process = [
    "UserAiMessageController.java",
    "PostController.java",
    "PostFavourController.java",
    "PostThumbController.java",
    "CommodityController.java",
    "CommodityOrderController.java",
    "CommodityScoreController.java",
    "CommodityTypeController.java",
    "UserCommodityFavoritesController.java",
    "PrivateMessageController.java",
    "NoticeController.java"
]

def refactor_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 移除 Result 导入
    content = re.sub(r'import com\.xyes\.springboot\.model\.Result;\n', '', content)
    
    # 替换方法返回类型: Result<Type> -> Type
    content = re.sub(r'public Result<([^>]+)> ', r'public \1 ', content)
    
    # 替换 Result.success(xxx) -> xxx
    content = re.sub(r'return Result\.success\(([^)]+)\);', r'return \1;', content)
    
    # 替换 Result.success() -> null
    content = re.sub(r'return Result\.success\(\);', r'return null;', content)
    
    # 保存文件
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"已处理: {os.path.basename(file_path)}")

# 处理所有文件
for filename in files_to_process:
    file_path = os.path.join(controller_dir, filename)
    if os.path.exists(file_path):
        refactor_file(file_path)
    else:
        print(f"文件不存在: {filename}")

print("\n批量处理完成!")
