import os
import shutil

src_dir = "/Users/yangcai/Documents/workspace/AutoCalc/app/src/main/res"

dst_dir = "/Users/yangcai/Documents/workspace/sandbox/calc/src/main/res"

fileName = "iconmv0001.webp"


# fileName = "frame_animation_btn_switch.xml"


def copy(fileName: str):
  print(fileName)
  for root, ds, fs in os.walk(src_dir):
    for f in fs:
      if f == fileName:
        fullPath = "{}/{}".format(root, f)
        dstFullPath = "{}/{}".format(dst_dir, fullPath[len(src_dir) + 1:])
        if not os.path.exists(dstFullPath[0:-len(f) - 1]):
          os.makedirs(dstFullPath[0:-len(f) - 1])
        shutil.copy(fullPath, dstFullPath)
        print(fullPath)


# copy("dialog_show_text.xml")

# for index in range(1,25):
#     fileName = "iconmv{}.webp".format(str(index).zfill(4))
#     copy(fileName)



dst_dir = "/Users/yangcai/Documents/workspace/sandbox/common/src/main/kotlin"
src_dir = "/Users/yangcai/Documents/workspace/sandbox"


def copy_kt(src_dir,dst_dir):
  for root, ds, fs in os.walk(src_dir):
    for f in fs:
      ktIndex = f.find(".kt")
      if ktIndex !=-1 and root.find("/java/") !=-1:
        fullPathJava = "{}/{}".format(root,f )
        print(fullPathJava)
        fullPathKt=fullPathJava.replace("/java/","/kotlin/")
        parentFile= fullPathKt[0:-len(f)-1]
        if not os.path.exists(parentFile):
          os.makedirs(parentFile)
        shutil.copy(fullPathJava, fullPathKt)
        print(fullPathKt)
        os.remove(fullPathJava)
     


def CEF(path):
    """
    CLean empty files, 清理空文件夹和空文件
    :param path: 文件路径，检查此文件路径下的子文件
    :return: None
    """
    files = os.listdir(path)  # 获取路径下的子文件(夹)列表
    for file in files:
        childFile = "{}/{}".format(path,file)
        
        if os.path.isdir(childFile):  # 如果是文件夹
            print ('Traversal at dir',childFile )
            if  len(os.listdir(childFile)) == 0:  # 如果子文件为空
                os.rmdir(childFile)  # 删除这个空文件夹
                print ('delete  dir',childFile )
            else:
               CEF(childFile)
        # elif os.path.isfile(childFile):  # 如果是文件
        #     if os.path.getsize(childFile) == 0:  # 文件大小为0
        #         os.remove(childFile)  # 删除这个文件
    print (path, 'Dispose over!')
for index in range(0,100):
   CEF(src_dir)

# copy_kt(src_dir,dst_dir)




