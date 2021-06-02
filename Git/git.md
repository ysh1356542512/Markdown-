# 查看历史记录

## git log

它会多屏显示：

空格向下翻页

b向上翻页

q退出

git log —pretty=oneline

git log —oneline

只显示之后的版本 不显示之前的版本

git reflog

会显示 移动到指定的版本步数

## 版本的前进和后退

第一种 基于索引值的操作

git reset —hard 哈希码

第二种 使用^

git reset -–hard HEAD^

^的数量 代表着 回退的次数

第三种 使用~

git reset -–hard HEAD~3

回退三个版本

reset命令的三个参数对比

soft参数

仅仅在本地库移动HEAD指针

mixed

在本地库移动指针

重置暂存区

hard

在本地库移动HEAD指针

重置暂存区

重置工作区

## 找回删除文件

前提就是 这个文件已经被提交到了本地库

## 比较文件差异

git diff  文件名

如果没有其它参数的话就表示把工作区和暂存区比较

git diff [本地库中的历史版本] [文件名]

表示将工作区中的文件和本地历史记录做比较

如果不指定文件名  会把多个文件都拿来比较

## 分支管理

同时并行的推进各个功能的开发，提高开发效率

git branch -v 查看所有分支

git branch 分支名  创建分支

git checkout 分支名   分支切换

合并分支

rebase和merge的区别：rebase是将你当前的修改作为[分支名]之后的修改（正如其名：在..基础上）

然后不产生提交，而merge是产生了一个新的提交。

站在需要合并的分支上 使用 git merge 分支名

强制修改分支的位置

git branch -f main HEAD~3

```
git branch -f bugF HEAD~3
```

上面的命令会将 main 分支强制指向 HEAD 的第 3 级父提交。

## 远程分支

git pull [远程主机名] [远程分支名]:[本地分支名] 相当于先执行git fetch [远程主机名]，然后再merge。

 ###  远程本地合并   

1.首先保证所有的代码都提交到远程分支了

2.从本地拉取一个最新的dev分支

3.git merge --squash 远程分支名 ，执行完后，现在就把远程分支上的代码同步到本地dev分支上了

4.有冲突解决冲突，然后 commit 。

5.push到远程dev

## 产生冲突

第一步 编辑文件删除特殊符号

第二步 把文件修改到满意的程度，修改退出

第三步 git add 文件名

第四步 git commit -m”日志”（此时一定不可以带文件名）

## Hash

其实就是一种加密算法

1. 不管输入的数据多大 得到的加密结果长度都是一样长的
2. 哈希算法确定，输入数据确定，输出数据能够保证不变
3. 哈希算法确定，输入数据有变化，输出数据一定有变化，而且通常变化很大 （即使是原始的数据只变化一点）
4. 哈希算法不可逆
5. Gir底层采用的是SHA-1算法

## 报错

### ==fatal: refusing to merge unrelated histories==

出现这个问题的最主要原因还是在于本地仓库和远程仓库实际上是独立的两个仓库。假如我之前是直接clone的方式在本地建立起远程github仓库的克隆本地仓库就不会有这问题了。

查阅了一下资料，发现可以在pull命令后紧接着使用--allow-unrelated-history选项来解决问题（该选项可以合并两个独立启动仓库的历史）
```git
git pull origin main --allow-unrelated-histories
```

#### OpenSSL SSL_read: Connection was reset, errno 10054

```git
git config --global http.sslVerify false
```

