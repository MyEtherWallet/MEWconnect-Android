### Release 1.0.14 (20041501)

- Minor updates and improvements

### Release 1.0.13 (20032601)

Updates to import process.

### Release 1.0.12 (20031101)

ANNOUNCEMENT: Full-fledged MEW wallet app is now available!
You can now buy, hold and send Ether and tokens directly in the app, without having to connect to MEW web.

Upgrade your MEWconnect app in just a few taps: download MEW wallet app, it will automatically detect if you are already using MEWconnect and offer you to import your account.

Launch MEWconnect after this update for more details.
P. S. If you don't want to upgrade, you can continue using MEWconnect app.

Questions? Please reach out to us via <a href="mailto:support@myetherwallet.com">support@myetherwallet.com</a>

### Release 1.0.11 (20030501)

- Minor improvements

### Release 1.0.10 (20021301)

- Stability improvements

### Release 1.0.9 (20020301)

- Fixed the issue that prevented token balances to be shown in some cases.
- The app will lock itself after a certain time, if you put it in the background.
- Updated Vision and Biometric libraries

### Release 1.0.8 (19102701)

- Fixed a rare crash when signing a transaction related to deploying a new smart contract

### Release 1.0.7 (19092401)

- Fixed a rare case with truncated words in Backup scenario
- Fixed connection scenario for Arabic languages

### Release 1.0.6 (19070301)

- Added biometric authentication (i.e. Fingerprint unlock and alike)
- Updates to ‘What's new’ screen
- Explicit ‘Log in’ button on ‘Enter password’ screen 
- Some minor changes on how fees are displayed on ‘Buy Ether’ screen.
- Cosmetic fixes on devices with a notch
- Other minor bugfixes and improvements

### Release 1.0.5 (19050901)

- Fixed crashes
- Fixed ‘User guide’ URL
- Fixed ENS confirmation issue

### Release 1.0.4 (19050101)

- Updated ‘Sign Message’ screen
- Tweaks to brute-force protection behavior
- Connection status is now visible in notifications drawer
- Phone will not go to sleep if the app is on the foreground
- Added MEW logo in QR code
- Some visual improvements

### Release 1.0.3 (19032801)

- Additional backup reminders (don’t forget to back up your wallet, otherwise you might lose your funds if you accidentally delete the app or lose your phone.)
- Additional confirmation if active network in MEW is not matching active network in MEWconnect
- You can now use MEWconnect to sign transactions for all network supported by MyEtherWallet.com
- Improved connection process in some cases
- Backup phrases of 12, 15, 18 and 21 words of length are now accepted in addition to the standard 24 word backup phrase. 
- Backup phrase can now be viewed in Info section in the app at any time (password protected). 
- Fixed document browse/upload button for BuyEther scenario
- What’s new alert so you can keep up with the latest changes
- Checksummed format for public addresses
- ‘No internet connection’ scenario
- Multiple minor bug fixes and improvements

### Release 1.0.2 (19020801)

- Implement timeout if user entered password five times incorrectly
- Fix key checkout issue

### Release 1.0.1 (19011701)

- Update version to 1.0.1
- Connection improvements
- Fix address truncation
- Reset WebRTC connection before reconnect
- Support for turn servers
- Refactor fragments transactions
- Fix crash on devices with Android 6 and Persian locale
- Update WebRTC library, update Kotlin, Gradle, Firebase Vision
- Bugfix, crash fix

### Release 1.0.0 (19010702)

- Update version to 1.0.0
- Fix amount in 'get buy order' request
- Make mnemonic phrase case unsensitive on Restore screen
- Fix bug when UI breaks after Info screen
- Fix payment status bug on History screen
- Show "Test network" instead of stock price for Ropsten network
- Force portrait orientation for Buy screen
- Allow buy if stock price not available
- Rate dialog
- Change wallet creation process
- Update application icon

### Release 1.0 (19010401)

- Update version to 1.0
- Check for absent currency
- Fix decimals field name
- Fix back action on 'Write these down' screen
- Changes in SignTx
- Fix UI issues on devices with notch
- Remove beta icons
- Note: please remove beta application before install

### Release 1.0.0.beta.4 (18120901)

### Devop

- Adaptation to devices with notch
- Change rates URL
- Fix Travis repo_slug

### Release 1.0.0.beta.2 (18112601)

### Bugs

- Fix wallet balance for Ropsten

### Release 1.0.0.beta.1 (18102501)

### Bugs

- Fix text size on backup screen

### Release 1.0.0.alpha.11

- Production package signature (remove previous build before install)
- Change version name format
- Show build number in Info
- Make sign message non-editable

### Release 1.0.0.alpha.10

- Fix message signing on production
- Remove logging from production builds

### Release 1.0.0.alpha.9

- Message signing
- Bugfix
- Note: please re-install application

### Release 1.0.0.alpha.8

- Prevent multiple transaction confirmation windows
- Fix service destroying when application paused
- Fix shadows width on buttons
- Refresh button with loading animation
- Copyright info
- Add finish button on confirmation screens
- Add contact info on scan screen
- Bugfix, UI improvements

### Release 1.0.0.alpha.7

- Bugfix, UI improvements

### Release 1.1.0.0.alpha.2

- Change app icon
- Change build number format
- Add sounds for scanning events
- Add address sharing screen
- Set fixed size for some text
- Change data storaging and generating
- UI improvements

### Release 1.0.0.alpha.1

- Ask password after application minimized on backup screen
- Hide content from task manager on backup screen
- Add ‘Try again’ button on Scan screen
- Force Roboto font family on generating animation
- Set lower connection timeout
- Multiply amount and price on Transaction confirmation screen
- Fix crash on Buy screen
- Close share screen after sharing
- Set wallet backed up after restore from mnemonic
- Pixel perfect for Info screen
- Filter purchase history items
- UI improvements
- Bugfix
- Testing
