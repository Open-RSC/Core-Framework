# Contributing to Open RSC

We'd love for you to contribute to our source code and to make Open RSC even better than it is
today! Here are the guidelines we'd like you to follow:

* [Code of Conduct](#coc)
* [Issues and Bugs](#issue)
* [Issue Submission Guidelines](#submit)
* [Merge Request Submission Guidelines](#submit-pr)
* [Suggested Developer Software](#software)
* [Developer Installation Process](#install)
* [Before You Start](#pre)
* [Code Standards](#standards)
* [Manual Install Script](#manual)

## <a name="coc"></a> Code of Conduct

Help us keep Open RSC both open and inclusive. Please read and follow our [Code of Conduct](https://gitlab.openrsc.com/open-rsc/Game/blob/2.0.0/CODE_OF_CONDUCT.md).

## <a name="issue"></a> Found an Issue or Bug?

If you find a bug in the source code, you can help us by submitting an issue to our
[GitLab Repository](https://gitlab.openrsc.com/open-rsc/Game). Even better, you can submit a Pull Request with a fix.

**Please see the [Submission Guidelines](#submit) below.**

## <a name="submit"></a> Issue Submission Guidelines
Before you submit your issue search the archive, maybe your question or bug report was already answered.

The "[new issue](https://gitlab.openrsc.com/open-rsc/Game/issues)" form contains a number of prompts that you should fill out to
make it easier to understand and categorize the issue.

In general, providing the following information will increase the chances of your issue being dealt
with quickly:

* **Overview of the Issue** - if an error is being thrown a non-minified stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **Reproduce the Error** - provide a live example or an unambiguous set of steps.
* **Related Issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

## <a name="submit-pr"></a> Merge Request Submission Guidelines
Before you submit your merge request consider the following guidelines:

* Search [GitLab](https://gitlab.openrsc.com/open-rsc/Game/merge_requests) for an open or closed Merge Request
  that relates to your submission. You don't want to duplicate effort.
* Create the [development environment](#install)
* Make your changes in a new git branch:

    ```shell
    git checkout -b my-fix-branch master
    ```

* Create your patch commit.
* Commit your changes using a descriptive commit message.

    ```shell
    git commit -a
    ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Push your branch to GitLab:

    ```shell
    git push origin my-fix-branch
    ```

* In GitLab, send a merge request to `Game:2.0.0`.


* If we suggest changes, then:

  * Make the required updates.
  * Commit your changes to your branch (e.g. `my-fix-branch`).
  * Push the changes to your GitLab repository (this will update your Merge Request).

    You can also amend the initial commits and force push them to the branch.

    ```shell
    git rebase master -i
    git push origin my-fix-branch -f
    ```

    This is generally easier to follow, but separate commits are useful if the Merge Request contains
    iterations that might be interesting to see side-by-side.

#### After your merge request is merged

After your merge request is merged, you can safely delete your branch and merge the changes
from the main (upstream) repository:

* Delete the remote branch on GitLab either through the GitLab web UI or your local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```

## <a name="software"></a> Suggested Developer Software

**IntelliJ IDEA Community** https://www.jetbrains.com/idea/download/

**Git Kraken** https://www.gitkraken.com/download

**Visual Studio Code** https://code.visualstudio.com/

**Sublime Text Editor** https://www.sublimetext.com/

## <a name="install"></a> Developer Installation Process

You will need to either use the set up script for Windows or Linux/Mac that comes in the repository or do it all manually.

If you choose to install everything manually and not use the set up scripts, the following are required to be installed to ensure we are all using the same development environment tools:

**MariaDB**, **OpenJDK 11**, **PHPMyAdmin**, **NGINX** or **Apache web server**, **PHP 7.2+**, **Git Kraken** or **git**

## <a name="pre"></a> Before You Start:

1. Download RSC+ to use for replaying authentic content: https://github.com/OrN/rscplus
2. Clone the Game repo and set up as you wish: https://gitlab.openrsc.com/open-rsc/Game
3. Download any replays you need: https://gitlab.openrsc.com/open-rsc/RSC-Plus-Replays

## <a name="standards"></a> Code Standards:

- Use single TABs, coupled with <tabspace>
- Claim or make tickets before working on features
- Make PRs from your local fork into the main repo
- Squash merge commits by using git pull --rebase before making your PR

## <a name="manual"></a> Manual Install Script for Ubuntu Linux by Christofosho:

https://gitlab.openrsc.com/open-rsc/Game/blob/2.0.0/scripts/archived/Installing-ORSC.txt
