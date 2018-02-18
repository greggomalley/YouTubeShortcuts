package com.neonburn.shortcuts;

import java.io.IOException;
import java.util.List;

interface TaskRunner {
//  List<YouTubeResult> runImpl() throws IOException;
  void run();
  String getErrorMessage();
}
