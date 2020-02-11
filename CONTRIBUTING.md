# Contributing to Open RSC

We'd love for you to contribute to our source code and to make Open RSC even better than it is
today! Here are the guidelines we'd like you to follow:

* [Merge Request Submission Guidelines](#submit-pr)
* [Suggested Developer Software](#software)
* [Developer Installation Process](#install)
* [Before You Start](#pre)
* [Code Standards](#standards)
* [Manual Install Script](#manual)

## <a name="submit-pr"></a> Merge Request Submission Guidelines
Before you submit your merge request consider the following guidelines:

* Search [GitLab](https://orsc.dev/open-rsc/Game/merge_requests) for an open or closed Merge Request
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

* In GitLab, send a merge request to `Game`.


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

**Git Fork** https://git-fork.com/

**MariaDB** https://mariadb.org/download/

**Sublime Text Editor** https://www.sublimetext.com/

## <a name="install"></a> Developer Installation Process

You will need to either use the set up script for Windows or Linux/Mac that comes in the repository or do it all manually.

If you choose to install everything manually and not use the set up scripts, the following are required to be installed to ensure we are all using the same development environment tools:

**MariaDB**, **OpenJDK**, **PHPMyAdmin**, **NGINX** or **Apache web server**, **PHP**, **Git Fork** or **git**

## <a name="pre"></a> Before You Start:

1. Download RSC+ to use for replaying authentic content: https://github.com/OrN/rscplus
2. Clone the Game repo and set up as you wish: https://orsc.dev/open-rsc/Game
3. Download any replays you need: https://orsc.dev/open-rsc/RSC-Plus-Replays

## <a name="standards"></a> Code Standards:

- Use single TABs, coupled with <tabspace>
- Claim or make tickets before working on features
- Make PRs from your local fork into the main repo
- Squash merge commits by using git pull --rebase before making your PRs
