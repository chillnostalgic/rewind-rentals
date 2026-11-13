# Rewind Rentals for Android TV and Fire TV

This folder is an Android app that shows Rewind Rentals full screen on a TV.
The app itself is `app/src/main/assets/index.html`. Everything else is the wrapper.

## Get the APK (no Android Studio needed)

1. Make a free GitHub account, then create a new **public** repository called `rewind-rentals`.
2. On the empty repository page, choose **uploading an existing file** and drag in everything
   from this folder, including the hidden `.github` folder. (If your computer hides it, use
   GitHub Desktop, or turn on hidden files first.) Commit to `main`.
3. Open the **Actions** tab. A "Build APK" run starts by itself and takes about 3 to 5 minutes.
4. When it's green, the APK is published at this fixed link:

   `https://github.com/YOUR-USERNAME/rewind-rentals/releases/latest/download/RewindRentals.apk`

## Put it on the TV

**Google TV / Android TV**
1. Install **Downloader** (by AFTVnews) from the Play Store on the TV.
2. Settings > Apps > Security & restrictions > Unknown sources > turn on Downloader.
   (On some TVs this is under Settings > Device preferences > Security & restrictions.)
3. Open Downloader, type the link above, press Go, then Install.

**Fire TV**
1. Install **Downloader** from the Amazon Appstore.
2. Settings > My Fire TV > Developer options > Install unknown apps > turn on Downloader.
   No Developer options? Go to Settings > My Fire TV > About and press OK on the device name 7 times.
3. Open Downloader, type the link above, press Go, then Install.

Rewind Rentals then appears with your other apps (on Google TV, look under Apps > See all).

**Tip:** that link is long to type with a remote. Make a short link for it (any link
shortener works) and type that into Downloader instead.

## Updating

Replace `app/src/main/assets/index.html` with the new version and commit. GitHub builds a new
APK and the same link always points at the newest one. Run Downloader again and install over
the top: your My tapes and settings are kept.

## Cast to a TV from your phone

Casting needs Google's cast kit, which only works on a secure web page in Chrome, not inside
this app. So the same page is also published as a website by `.github/workflows/publish-website.yml`.

1. In the repository, open **Settings**, then **Pages**, and set **Source** to **GitHub Actions**.
2. Push any change (or run the "Publish website" workflow by hand). It takes about a minute.
3. The site appears at `https://YOUR-USERNAME.github.io/REPO-NAME/`.

On your phone, open that address in **Chrome** (not the app). If a Chromecast or Google TV is on
the same Wi-Fi, a **Cast** button appears in the header. Pick your TV, then play a tape or a
channel: the TV plays it and your phone becomes the remote. Press the Cast button again to stop.

Inside the app, the Cast button simply doesn't appear. To get Rewind Rentals itself onto a TV,
install the APK on the TV, or use your phone's own "Cast screen" / screen mirroring.

## On the remote

Arrows move, OK selects, Back closes the TV or player (and exits from the shop).
In TV mode: up and down change channel, number keys jump to a channel.

## Notes

- The app is signed with a test key (in `keystore/`) so updates install over each other. It's
  fine for testing at home. A Play Store or Amazon Appstore release would need a proper key.
- Needs Android 5.1 / Fire OS 5 or newer, and an internet connection.
