import myAxios from "../plugins/myAxios.js";

export const getCurrentUser = async ()=>{
    return (await myAxios.get("/user/current")).data?.data
}

export const getLocalStorageUserId = () => {
    return localStorage.getItem('userId')
}

export const DateToDetailTime = (val) => {
    const date = new Date(val);
    return date.toLocaleDateString() + " " + date.toLocaleTimeString()
}

export const DateToChatTime = (val) => {
    const date = new Date(val)
    let dateDate = date.toLocaleDateString(), dateTime = date.toLocaleTimeString()
    let time = dateTime.substring(0, dateTime.lastIndexOf(":"))
    let toDay = new Date()
    let toDayDate = toDay.toLocaleDateString()

    if (toDayDate === dateDate) {
        //  若日期相同，返回具体时间
        return time
    }else if(isYesterday(date)){
        //  判断是否为昨天
        return "昨天 " + time
    }else if(isBeforeYesterday(date)){
        //  判断是否为前天
        return "前天 " + time
    } else if (date.getFullYear() === toDay.getFullYear()){
        //  若在同一年
        if (getWeek(toDay) === getWeek(date)) {
            //  若在同一礼拜，返回星期 + 具体时间
            return "星期" + date.getDate() + " " + date.toLocaleTimeString()
        } else {
            //  同一年不同礼拜，返回月日
            return dateDate.substring(dateDate.indexOf("/")+1)
                .replace("/","-")
        }
    }
    //  不同年
    return dateDate
}

function isBeforeYesterday(date) {
    const currentDate = new Date();  // 获取当前日期
    const beforeYesterday = new Date(currentDate);  // 创建前天的日期对象
    beforeYesterday.setDate(currentDate.getDate() - 2);  // 将前天的日期设置为当前日期减去2天

    // 比较日期是否相等
    return (
        date.getDate() === beforeYesterday.getDate() &&
        date.getMonth() === beforeYesterday.getMonth() &&
        date.getFullYear() === beforeYesterday.getFullYear()
    );
}

function isYesterday(date) {
    const currentDate = new Date();  // 获取当前日期
    const yesterday = new Date(currentDate);  // 创建昨天的日期对象
    yesterday.setDate(currentDate.getDate() - 1);  // 将昨天的日期设置为当前日期减去1天

    // 比较日期是否相等
    return (
        date.getDate() === yesterday.getDate() &&
        date.getMonth() === yesterday.getMonth() &&
        date.getFullYear() === yesterday.getFullYear()
    );
}

// 获取日期的周数
function getWeek(date) {
    // 创建一个新的日期对象，将其设置为给定日期的副本，以便不会改变原始日期对象
    const newDate = new Date(date.getTime());

    // 将日期设置为一周的第一天（星期日）
    newDate.setDate(newDate.getDate() - newDate.getDay());

    // 获取年份的第一天
    const firstDayOfYear = new Date(newDate.getFullYear(), 0, 1);

    // 计算日期与年份第一天之间的天数差
    const diff = Math.floor((newDate - firstDayOfYear) / (24 * 60 * 60 * 1000));

    // 计算周数（向下取整）
    const week = Math.floor(diff / 7) + 1;

    return week;
}

export const getTags = (tags) => {
    if(tags)
        return JSON.parse(tags)
    return null
}