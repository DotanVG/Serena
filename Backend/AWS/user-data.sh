#!/bin/bash

# Function to log messages
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | sudo tee -a /var/log/user-data.log
}

# Update the system
log "Updating system"
sudo yum update -y

# Install Node.js version 20.x 
log "Installing Node.js"
curl -sL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs
node -v >> /var/log/user-data.log 2>&1

# Install Git
log "Installing Git"
sudo yum install -y git
git --version >> /var/log/user-data.log 2>&1

# Create the directory for the repository (if it doesn't exist)
log "Creating directory"
sudo mkdir -p /home/ec2-user/Serena

# Clone your repository
log "Cloning repository"
if sudo git clone https://github.com/DotanVG/Serena.git /home/ec2-user/Serena; then
    log "Repository cloned successfully"
else
    log "Failed to clone repository"
    exit 1
fi

# Set correct ownership for the cloned repository
log "Setting ownership"
sudo chown -R ec2-user:ec2-user /home/ec2-user/Serena

# Set up your Node.js application
log "Setting up Node.js application"
cd /home/ec2-user/Serena/Backend || { log "Backend directory not found"; exit 1; }

# List contents of the current directory
log "Contents of Backend directory:"
ls -la >> /var/log/user-data.log 2>&1

# Install PM2 globally
log "Installing PM2"
sudo npm install -g pm2

# Install dependencies
log "Installing dependencies"
if sudo -u ec2-user npm install; then
    log "Dependencies installed successfully"
else
    log "Failed to install dependencies"
    exit 1
fi

# Set environment variables
log "Setting environment variables"
sudo -u ec2-user bash -c 'echo "export mongoURI=\"mongodb+srv://levinitai94:AOJX6DJTAnLogtcc@serena.eiebglf.mongodb.net/?retryWrites=true&w=majority&appName=Serena\"" >> ~/.bash_profile'
sudo -u ec2-user bash -c 'echo "export jwtSecret=\"AOJX6DJTAnLogtcc\"" >> ~/.bash_profile'

# Reload the bash profile
log "Reloading bash profile"
source /home/ec2-user/.bash_profile

# Start your application with PM2
log "Starting application with PM2"
sudo -u ec2-user bash -c 'source ~/.bash_profile && cd /home/ec2-user/Serena/Backend && pm2 start server.js --name "serena-backend"'

# Save PM2 process list and set to start on reboot
log "Saving PM2 process list and setting up startup"
sudo env PATH=$PATH:/usr/bin pm2 startup systemd -u ec2-user --hp /home/ec2-user
sudo -u ec2-user pm2 save

log "User data script completed"