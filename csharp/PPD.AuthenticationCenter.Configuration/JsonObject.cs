using System;
using System.Collections.Generic;
using System.Text;
using System.Web.Script.Serialization;

namespace PPD.AuthenticationCenter.Configuration
{
    public class JsonObject
    {
        /// <summary>
        /// 解析JSON字串
        /// </summary>
        public static JsonObject Parse(string json)
        {
            var js = new JavaScriptSerializer();

            object obj = js.DeserializeObject(json);

            return new JsonObject()
            {
                Value = obj
            };
        }

        /// <summary>
        /// 取对象的属性
        /// </summary>
        public JsonObject this[string key]
        {
            get
            {
                var dict = this.Value as Dictionary<string, object>;
                if (dict != null && dict.ContainsKey(key))
                {
                    return new JsonObject { Value = dict[key] };
                }

                return new JsonObject();
            }

        }

        /// <summary>
        /// 取数组
        /// </summary>
        public JsonObject this[int index]
        {
            get
            {
                var array = this.Value as object[];
                if (array != null && array.Length > index)
                {
                    return new JsonObject { Value = array[index] };
                }
                return new JsonObject();
            }
        }

        /// <summary>
        /// 将值以希望类型取出
        /// </summary>
        public T GetValue<T>()
        {
            return (T)Convert.ChangeType(Value, typeof(T));
        }

        /// <summary>
        /// 取出字串类型的值
        /// </summary>
        public string Text()
        {
            return Convert.ToString(Value);
        }

        /// <summary>
        /// 取出数值
        /// </summary>
        public double Number()
        {
            return Convert.ToDouble(Value);
        }

        /// <summary>
        /// 取出整型
        /// </summary>
        public int Integer()
        {
            return Convert.ToInt32(Value);
        }

        /// <summary>
        /// 取出布尔型
        /// </summary>
        public bool Boolean()
        {
            return Convert.ToBoolean(Value);
        }

        /// <summary>
        /// 值
        /// </summary>
        public object Value
        {
            get;
            private set;
        }

        /// <summary>
        /// 如果是数组返回数组长度
        /// </summary>
        public int Length
        {
            get
            {
                var array = this.Value as object[];
                if (array != null)
                {
                    return array.Length;
                }
                return 0;
            }
        }

    }
}
