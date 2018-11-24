# Contributing to Open RSC

We'd love for you to contribute to our source code and to make Open RSC even better than it is
today! Here are the guidelines we'd like you to follow:

* [Code of Conduct](#coc)
* [Issues and Bugs](#issue)
* [Issue Submission Guidelines](#submit)
* [Pull Request Submission Guidelines](#submit-pr)
* [Suggested Developer Software](#software)
* [Developer Installation Process](#install)
* [Before You Start](#pre)
* [Code Standards](#standards)
* [Manual Install Script](#manual)

## <a name="coc"></a> Code of Conduct

Help us keep Open RSC both open and inclusive. Please read and follow our [Code of Conduct](https://github.com/open-rsc/Game/blob/2.0.0/CODE_OF_CONDUCT.md).

## <a name="issue"></a> Found an Issue or Bug?

If you find a bug in the source code, you can help us by submitting an issue to our
[GitHub Repository](https://github.com/open-rsc/Game). Even better, you can submit a Pull Request with a fix.

**Please see the [Submission Guidelines](#submit) below.**

## <a name="submit"></a> Issue Submission Guidelines
Before you submit your issue search the archive, maybe your question or bug report was already answered.

The "[new issue](https://github.com/open-rsc/Game/issues/new/choose)" form contains a number of prompts that you should fill out to
make it easier to understand and categorize the issue.

In general, providing the following information will increase the chances of your issue being dealt
with quickly:

* **Overview of the Issue** - if an error is being thrown a non-minified stack trace helps
* **Motivation for or Use Case** - explain why this is a bug for you
* **Reproduce the Error** - provide a live example or an unambiguous set of steps.
* **Related Issues** - has a similar issue been reported before?
* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit)

## <a name="submit-pr"></a> Pull Request Submission Guidelines
Before you submit your pull request consider the following guidelines:

* Search [GitHub](https://github.com/open-rsc/game/pulls) for an open or closed Pull Request
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

* Push your branch to GitHub:

    ```shell
    git push origin my-fix-branch
    ```

* In GitHub, send a pull request to `Game:2.0.0`. This will trigger the check of the Travis integration.

* If you find that the Travis integration has failed, look into the logs on Travis to find out
if your changes caused test failures, the commit message was malformed etc. If you find that the
tests failed or times out for unrelated reasons, you can ping a team member so that the build can be
restarted.

* If we suggest changes, then:

  * Make the required updates.
  * Commit your changes to your branch (e.g. `my-fix-branch`).
  * Push the changes to your GitHub repository (this will update your Pull Request).

    You can also amend the initial commits and force push them to the branch.

    ```shell
    git rebase master -i
    git push origin my-fix-branch -f
    ```

    This is generally easier to follow, but separate commits are useful if the Pull Request contains
    iterations that might be interesting to see side-by-side.

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

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

**Atom Editor** https://atom.io

## <a name="install"></a> Developer Installation Process

You will need to either use the set up script for Windows or Linux/Mac that comes in the repository or do it all manually.

If you choose to install everything manually and not use the set up scripts, the following are required to be installed to ensure we are all using the same development environment tools:

**MariaDB**, **OpenJDK 11**, **PHPMyAdmin**, **Nginx** or **Apache web server**, **PHP 7.2+**, **Git Kraken** or **git**

## <a name="pre"></a> Before You Start:

1. Download RSC+ to use for replaying authentic content: https://github.com/OrN/rscplus
2. Clone the Game repo and set up as you wish: https://github.com/Open-RSC/Game
3. Download any replays you need: https://github.com/Open-RSC/RSC-Plus-Replays

## <a name="standards"></a> Code Standards:

- Use single TABs, coupled with <tabspace>
- Claim or make tickets before working on features
- Make PRs from your local fork into the main repo
- Squash merge commits by using git pull --rebase before making your PR

## <a name="manual"></a> Manual Install Script for Ubuntu Linux by Christofosho:

[Installing-ORSC.txt](https://raw.githubusercontent.com/Open-RSC/Game/2.0.0/scripts/Installing-ORSC.txt)