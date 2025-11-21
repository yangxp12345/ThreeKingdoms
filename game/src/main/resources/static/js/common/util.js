String.prototype.hashCode = function() {
    let hash = 0;
    if (this.length === 0) return hash;
    for (let i = 0; i < this.length; i++) {
        let char = this.charCodeAt(i);
        hash = (hash<<5) - hash + char;
        hash = hash & hash; // 将高位溢出转换为低位
    }
    return hash;
};