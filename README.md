> 本Demo主要目的为学习及研究自定义ViewGroup,通过实现一种拼图游戏而熟悉ViewGroup的onMeasure,onLayout及onTouchEvent的处理
- 游戏原型

![image](http://routee.oss-cn-shanghai.aliyuncs.com/18-4-28/83620842.jpg)

- demo展示

![image](http://routee.oss-cn-shanghai.aliyuncs.com/18-4-28/95277908.jpg)

> 实现说明及注意点
- 使用RelaytiveLayout，通过addView(ImageView.setImageBitmap(Bitmap))方式添加所有小方块，其中添加的View需设置合适的LayoutParams
- 难点:简单的使用ColleCtions.suffle()打乱各个方块将有一半的几率导致游戏无法完成，需使用Collections.swap(int i,int j)方式打乱各模块
``` java
private void sortBlocks(List<Units> bms) {
        if (mColumns % 2 == 0) {
            for (int i = 0; i < 40; i++) {
                int index = (int) (Math.random() * bms.size());
                if (index % mColumns + 2 > mColumns) {
                    if (index - 2 >= 0) {
                        Collections.swap(bms, index, index - 2);
                    } else {
                        Collections.swap(bms, index, index + mColumns * 2);
                    }
                } else {
                    if (index + 2 < bms.size()) {
                        Collections.swap(bms, index, index + 2);
                    } else {
                        Collections.swap(bms, index, index - mColumns * 2);
                    }
                }
            }
        } else {
            for (int i = 0; i < 60; i++) {
                int index = (int) (Math.random() * bms.size());
                if (index + 2 >= bms.size()) {
                    Collections.swap(bms, index, index - 2);
                } else {
                    Collections.swap(bms, index, index + 2);
                }
            }
        }
    }
```
- 错误示例(**相邻两个滑块直接进行奇数数次交换位置，最后将会剩下两个滑块位置颠倒，且无法通过移动滑块位置还原**)

![image](http://routee.oss-cn-shanghai.aliyuncs.com/18-4-28/33864304.jpg)


- 正确示例

![image](http://routee.oss-cn-shanghai.aliyuncs.com/18-4-28/74598831.jpg)

> 游戏说明
- 可以通过easy和hard减少和增加游戏难度（最易为3x3难度，最难为5x5难度）
- 可以通过按住showpic按钮显示完成后效果图片

> 其他说明
- 使用了[RxPermission](https://github.com/tbruyelle/RxPermissions)对不同版本手机进行了拍照及文件读取权限处理
- 使用了[ImagePicker](https://github.com/jeasonlzy/ImagePicker)图片选择框架选择手机内存图片或拍照
