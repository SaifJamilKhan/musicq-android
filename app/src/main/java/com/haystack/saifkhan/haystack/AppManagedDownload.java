package com.haystack.saifkhan.haystack;
//https://github.com/axet/vget


public class AppManagedDownload {
//
//    VideoInfo info;
//    long last;
//
//    public void run(String url, File path) {
//        try {
//            AtomicBoolean stop = new AtomicBoolean(false);
//            Runnable notify = new Runnable() {
//                @Override
//                public void run() {
//                    VideoInfo i1 = info;
//                    DownloadInfo i2 = i1.getInfo();
//
//                    // notify app or save download state
//                    // you can extract information from DownloadInfo info;
//                    switch (i1.getState()) {
//                        case EXTRACTING:
//                        case EXTRACTING_DONE:
//                        case DONE:
//                            System.out.println(i1.getState() + " " + i1.getVideoQuality());
//                            break;
//                        case RETRYING:
//                            System.out.println(i1.getState() + " " + i1.getDelay());
//                            break;
//                        case DOWNLOADING:
//                            long now = System.currentTimeMillis();
//                            if (now - 1000 > last) {
//                                last = now;
//
//                                String parts = "";
//
//                                List<DownloadInfo.Part> pp = i2.getParts();
//                                if (pp != null) {
//                                    // multipart download
//                                    for (DownloadInfo.Part p : pp) {
//                                        if (p.getState().equals(DownloadInfo.Part.States.DOWNLOADING)) {
//                                            parts += String.format("Part#%d(%.2f) ", p.getNumber(), p.getCount()
//                                                    / (float) p.getLength());
//                                        }
//                                    }
//                                }
//
//                                System.out.println(String.format("%s %.2f %s", i1.getState(),
//                                        i2.getCount() / (float) i2.getLength(), parts));
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            };
//
//            info = new VideoInfo(new URL(url));
//
//            // [OPTIONAL] limit maximum quality, or do not call this function if
//            // you wish maximum quality available.
//            //
//            // if youtube does not have video with requested quality, program
//            // will raise an exception.
//            VideoInfoUser user = new VideoInfoUser();
//            user.setUserQuality(VideoInfo.VideoQuality.p480);
//
//            VGet v = new VGet(info, path);
//
//            // [OPTIONAL] call v.extract() only if you d like to get video title
//            // before start download. or just skip it.
//            v.extract(user, stop, notify);
//            System.out.println(info.getTitle());
//
//            v.download(user, stop, notify);
//        } catch (RuntimeException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void main(String[] args) {
//        AppManagedDownload e = new AppManagedDownload();
//        // ex: http://www.youtube.com/watch?v=Nj6PFaDmp6c
//        String url = args[0];
//        // ex: /Users/axet/Downloads/
//        String path = args[1];
//        e.run(url, new File(path));
//    }
}