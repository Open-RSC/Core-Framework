## Guide for running Open RSC on MacOS:

# Step 1

First, you will need to install Homebrew for Mac in Terminal via <a href="https://brew.sh">https://brew.sh</a>

To do so, open Terminal, copy and paste the following, and then press enter:
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

Now copy and paste the following in Terminal and press enter:
```
brew install adoptopenjdk/openjdk
```

This installs Open JDK 12. If you have an earlier version of Java installed, such as Oracle Java 8, it is highly suggested to uninstall that first. Next, copy and paste the following in Terminal and press enter:
```
brew install docker docker-compose
```

# Step 2

You are now ready to use "Start-Linux.sh" (Skip the option for Install). Open Terminal, navigate to this folder, and execute:
```
./Start-Linux.sh
```