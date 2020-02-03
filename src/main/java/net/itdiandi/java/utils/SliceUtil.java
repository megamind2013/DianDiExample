package net.itdiandi.java.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/** 
* @ProjectName Utils
* @PackageName net.itdiandi.utils
* @ClassName SliceUtil
* @Description 分片工具 
* @author 刘吉超
* @date 2016-02-24 19:47:48
*/
public class SliceUtil {
	public static final String DATE_TIME_FORMAT_19 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 按照天划分得到工单集合 2015-04-28 01:10:05 - 2015-04-29 09:30:05 --> (1)2015-04-28
	 * 01:10:05 - 2015-04-28 23:59:59 (2)2015-04-29 00:00:00 - 2015-04-29
	 * 09:30:05
	 * 
	 * @return String
	 * @throws CloneNotSupportedException
	 * @see [类、类#方法、类#成员]
	 */
	public List<NetLogConditionVO> getFlowListByTime(NetLogConditionVO netLogVO)
			throws CloneNotSupportedException {
		// 按时间分片的上网日志vo
		List<NetLogConditionVO> netLogByTimeList = new ArrayList<NetLogConditionVO>();

		Calendar startTime = Calendar.getInstance();
		startTime.setTime(stringToDate(netLogVO.getQryStartTime(),
				DATE_TIME_FORMAT_19));

		Calendar endTime = Calendar.getInstance();
		endTime.setTime(stringToDate(netLogVO.getQryEndTime(),
				DATE_TIME_FORMAT_19));

		while (startTime.compareTo(endTime) <= 0) {
			NetLogConditionVO tempVO = netLogVO.clone();

			tempVO.setQryStartTime(dateToString(startTime.getTime(),
					DATE_TIME_FORMAT_19));

			startTime.set(startTime.get(Calendar.YEAR),
					startTime.get(Calendar.MONTH),
					startTime.get(Calendar.DATE) + 1, 0, 0, -1);

			Calendar tempTime = startTime.compareTo(endTime) < 0 ? startTime
					: endTime;
			tempVO.setQryEndTime(dateToString(tempTime.getTime(),
					DATE_TIME_FORMAT_19));

			startTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND) + 1);

			netLogByTimeList.add(tempVO);
		}

		return netLogByTimeList;
	}

	/**
	 * 将时间转换为字符串
	 * 
	 * @param d
	 *            待转换时间
	 * @param format
	 *            字符串格式
	 * @return
	 * @date 2013-8-30
	 */
	public static final String dateToString(Date d, String format) {
		String dateStr = "";

		if (format.equals(DATE_TIME_FORMAT_19)) {
			dateStr = FORMAT_DATE_TIME_FORMAT_19.get().format(d);
		}

		return dateStr;
	}

	/**
	 * 将字符串转换为时间
	 * 
	 * @param string
	 *            待转换字符串
	 * @param format
	 *            字符串格式
	 * @return
	 * @author x00188614
	 * @date 2013-8-30
	 */
	public static final Date stringToDate(String string, String format) {
		if (StringUtils.isBlank(string)) {
			return null;
		}

		if (format.equals(DATE_TIME_FORMAT_19)) {
			try {
				return FORMAT_DATE_TIME_FORMAT_19.get().parse(string);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private static final ThreadLocal<SimpleDateFormat> FORMAT_DATE_TIME_FORMAT_19 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_TIME_FORMAT_19);
		}
	};
}

class NetLogConditionVO implements Cloneable {
	private String qryStartTime;

	private String qryEndTime;

	public String getQryStartTime() {
		return qryStartTime;
	}

	public void setQryStartTime(String qryStartTime) {
		this.qryStartTime = qryStartTime;
	}

	public String getQryEndTime() {
		return qryEndTime;
	}

	public void setQryEndTime(String qryEndTime) {
		this.qryEndTime = qryEndTime;
	}

	@Override
	public NetLogConditionVO clone() throws CloneNotSupportedException {
		return (NetLogConditionVO) super.clone();
	}
}