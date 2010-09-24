using System;

namespace FormulaCreator
{
    public class SimpleTriplet : ITriplet
    {
        public SimpleTriplet(int a, int b, int c)
        {
            if (a == b || b == c || a == c)
                throw new ArgumentException("a == b || b == c || a == c");

            _a = a;
            _b = b;
            _c = c;

            a = Math.Abs(a);
            b = Math.Abs(b);
            c = Math.Abs(c);

            if (a < b && b < c) InitNameValue(_a, _b, _c); else
            if (a < c && c < b) InitNameValue(_a, _c, _b); else
            if (c < a && a < b) InitNameValue(_c, _a, _b); else
            if (c < b && b < a) InitNameValue(_c, _b, _a); else
            if (b < a && a < c) InitNameValue(_b, _a, _c); else
                                InitNameValue(_b, _c, _a);
        }

        private void InitNameValue(int a, int b, int c)
        {
            _name = string.Format("{0},{1},{2}",
                                 Math.Abs(a),
                                 Math.Abs(b),
                                 Math.Abs(c));

            _value = string.Format("{0}={1},{2}={3},{4}={5}",
                                 Math.Abs(a), a < 0,
                                 Math.Abs(b), b < 0,
                                 Math.Abs(c), c < 0);
        }

        private string _value;

        private readonly int _a;
        private readonly int _b;
        private readonly int _c;

        public int AName { get { return Math.Abs(_a); } }
        public int BName { get { return Math.Abs(_b); } }
        public int CName { get { return Math.Abs(_c); } }

        public bool NotA { get { return _a < 0; } }
        public bool NotB { get { return _b < 0; } }
        public bool NotC { get { return _c < 0; } }

        private string _name;
        public string CanonicalName
        {
            get { return _name; }
        }

        public bool HasSameVariablesAs(ITriplet triplet)
        {
            return CanonicalName.Equals(triplet.CanonicalName);
        }

        public ITriplet TransformAs(ITriplet triplet)
        {
            int a = _a;
            int b = _b;
            int c = _c;

            if (triplet.AName != Math.Abs(a))
            {
                if (triplet.AName == Math.Abs(b)) Helper.Swap(ref a, ref b); else Helper.Swap(ref a, ref c);
            }
            if (triplet.BName != Math.Abs(b))
            {
                if (triplet.BName == Math.Abs(a)) Helper.Swap(ref b, ref a); else Helper.Swap(ref b, ref c);
            }
            if (triplet.AName != Math.Abs(a))
            {
                if (triplet.AName == Math.Abs(b)) Helper.Swap(ref a, ref b); else Helper.Swap(ref a, ref c);
            }
            return a == _a && b == _b && c == _c ? this : new SimpleTriplet(a, b, c);
        }

        public bool HasVariable(int varName)
        {
            return AName == varName || BName == varName || CName == varName;
        }

        public override string ToString()
        {
            return string.Format("{0},{1},{2}", _a, _b, _c);
        }

        public override bool Equals(object obj)
        {
            if (obj == null || GetType() != obj.GetType())
            {
                return false;
            }

            return _value.Equals(((SimpleTriplet) obj)._value);
        }

        public override int GetHashCode()
        {
            return _value.GetHashCode();
        }
    }
}